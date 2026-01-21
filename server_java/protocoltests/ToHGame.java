import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import protocol.messages.*;
import protocol.utils.Utils;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ToH Game")
class ToHGame {

    private final static Properties PROPS = new Properties();

    private Socket socketUser1, socketUser2;
    private BufferedReader inUser1, inUser2;
    private PrintWriter outUser1, outUser2;
    private static String host;
    private static int port;
    private static int pingTimeMsDeltaAllowed;

    @BeforeAll
    static void setupAll() throws IOException {
        InputStream in = ToHGame.class.getResourceAsStream("testconfig.properties");
        PROPS.load(in);
        host = PROPS.getProperty("host");
        port = Integer.parseInt(PROPS.getProperty("port"));
        pingTimeMsDeltaAllowed = Integer.parseInt(PROPS.getProperty("ping_time_ms_delta_allowed"));
        in.close();
    }

    @BeforeEach
    void setup() throws IOException {
        socketUser1 = new Socket(host, port);
        inUser1 = new BufferedReader(new InputStreamReader(socketUser1.getInputStream()));
        outUser1 = new PrintWriter(socketUser1.getOutputStream(), true);

        socketUser2 = new Socket(host, port);
        inUser2 = new BufferedReader(new InputStreamReader(socketUser2.getInputStream()));
        outUser2 = new PrintWriter(socketUser2.getOutputStream(), true);
    }

    @AfterEach
    void cleanup() throws IOException {
        socketUser1.close();
        socketUser2.close();
    }

    // Helper to log in both users
    private void loginBothUsers() throws JsonProcessingException {
        receiveLineWithTimeout(inUser1, "HI expected");
        receiveLineWithTimeout(inUser2, "HI expected");

        outUser1.println(Utils.objectToMessage(new Logon("user1")));
        outUser1.flush();
        receiveLineWithTimeout(inUser1, "LOGON_RESP expected");

        outUser2.println(Utils.objectToMessage(new Logon("user2")));
        outUser2.flush();
        receiveLineWithTimeout(inUser2, "LOGON_RESP expected");

        // user1 receives JOINED for user2
        receiveLineWithTimeout(inUser1, "JOINED expected");
    }

    @Nested
    @DisplayName("Invite")
    class Invite {

        @Test
        @DisplayName("user can invite another user")
        void user_can_invite_another_user() throws JsonProcessingException {
            loginBothUsers();

            // user1 invites user2
            outUser1.println(Utils.objectToMessage(new ToHInviteReq("user2")));
            outUser1.flush();

            // user1 gets OK response
            String response = receiveLineWithTimeout(inUser1, "TOH_RESP expected");
            ToHResp resp = Utils.messageToObject(response);
            assertEquals("OK", resp.status());

            // user2 receives the invite
            String invite = receiveLineWithTimeout(inUser2, "TOH_INVITE expected");
            ToHInvite toHInvite = Utils.messageToObject(invite);
            assertEquals("user1", toHInvite.from());
        }

        @Test
        @DisplayName("error when inviting non-existent user")
        void error_when_inviting_nonexistent_user() throws JsonProcessingException {
            loginBothUsers();

            // user1 invites non-existent user
            outUser1.println(Utils.objectToMessage(new ToHInviteReq("nobody")));
            outUser1.flush();

            String response = receiveLineWithTimeout(inUser1, "TOH_RESP expected");
            ToHResp resp = Utils.messageToObject(response);
            assertEquals("ERROR", resp.status());
            assertEquals(10000, resp.code());
        }

        @Test
        @DisplayName("error when inviting yourself")
        void error_when_inviting_yourself() throws JsonProcessingException {
            loginBothUsers();

            // user1 invites themselves
            outUser1.println(Utils.objectToMessage(new ToHInviteReq("user1")));
            outUser1.flush();

            String response = receiveLineWithTimeout(inUser1, "TOH_RESP expected");
            ToHResp resp = Utils.messageToObject(response);
            assertEquals("ERROR", resp.status());
            assertEquals(10001, resp.code());
        }
    }

    @Nested
    @DisplayName("Accept")
    class Accept {

        @Test
        @DisplayName("game starts when invite is accepted")
        void game_starts_when_invite_is_accepted() throws JsonProcessingException {
            loginBothUsers();

            // user1 invites user2
            outUser1.println(Utils.objectToMessage(new ToHInviteReq("user2")));
            outUser1.flush();
            receiveLineWithTimeout(inUser1, "TOH_RESP expected");
            receiveLineWithTimeout(inUser2, "TOH_INVITE expected");

            // user2 accepts
            outUser2.println("TOH_ACCEPT");
            outUser2.flush();

            // user2 receives start message
            String startMsg = receiveLineWithTimeout(inUser2, "TOH_START expected");
            ToHStart start = Utils.messageToObject(startMsg);
            assertEquals("user1", start.opponent());
            assertEquals(1, start.round());

            // user1 also receives start message
            String startMsg1 = receiveLineWithTimeout(inUser1, "TOH_START expected");
            ToHStart start1 = Utils.messageToObject(startMsg1);
            assertEquals("user2", start1.opponent());
            assertEquals(1, start1.round());
        }

        @Test
        @DisplayName("error when accepting without invite")
        void error_when_accepting_without_invite() throws JsonProcessingException {
            loginBothUsers();

            // user2 tries to accept without having an invite
            outUser2.println("TOH_ACCEPT");
            outUser2.flush();

            String response = receiveLineWithTimeout(inUser2, "TOH_RESP expected");
            ToHResp resp = Utils.messageToObject(response);
            assertEquals("ERROR", resp.status());
            assertEquals(10003, resp.code());
        }
    }

    @Nested
    @DisplayName("Decline")
    class Decline {

        @Test
        @DisplayName("inviter gets notified when declined")
        void inviter_gets_notified_when_declined() throws JsonProcessingException {
            loginBothUsers();

            // user1 invites user2
            outUser1.println(Utils.objectToMessage(new ToHInviteReq("user2")));
            outUser1.flush();
            receiveLineWithTimeout(inUser1, "TOH_RESP expected");
            receiveLineWithTimeout(inUser2, "TOH_INVITE expected");

            // user2 declines
            outUser2.println("TOH_DECLINE");
            outUser2.flush();

            // user1 receives decline notification
            String declined = receiveLineWithTimeout(inUser1, "TOH_DECLINED expected");
            ToHDeclined toHDeclined = Utils.messageToObject(declined);
            assertEquals("user2", toHDeclined.from());
        }
    }

    @Nested
    @DisplayName("Choice")
    class Choice {

        @Test
        @DisplayName("error when making choice while not in game")
        void error_when_making_choice_not_in_game() throws JsonProcessingException {
            loginBothUsers();

            // user1 tries to make a choice without being in a game
            outUser1.println(Utils.objectToMessage(new ToHChoiceReq("heads")));
            outUser1.flush();

            String response = receiveLineWithTimeout(inUser1, "TOH_RESP expected");
            ToHResp resp = Utils.messageToObject(response);
            assertEquals("ERROR", resp.status());
            assertEquals(10005, resp.code());
        }

        @Test
        @DisplayName("error when making invalid choice")
        void error_when_making_invalid_choice() throws JsonProcessingException {
            loginBothUsers();

            // Start a game first
            outUser1.println(Utils.objectToMessage(new ToHInviteReq("user2")));
            outUser1.flush();
            receiveLineWithTimeout(inUser1, "TOH_RESP expected");
            receiveLineWithTimeout(inUser2, "TOH_INVITE expected");

            outUser2.println("TOH_ACCEPT");
            outUser2.flush();
            receiveLineWithTimeout(inUser2, "TOH_START expected");
            receiveLineWithTimeout(inUser1, "TOH_START expected");

            // user1 makes invalid choice
            outUser1.println(Utils.objectToMessage(new ToHChoiceReq("banana")));
            outUser1.flush();

            String response = receiveLineWithTimeout(inUser1, "TOH_RESP expected");
            ToHResp resp = Utils.messageToObject(response);
            assertEquals("ERROR", resp.status());
            assertEquals(10004, resp.code());
        }
    }

    @Nested
    @DisplayName("Not Logged In")
    class NotLoggedIn {

        @Test
        @DisplayName("error when inviting without being logged in")
        void error_when_inviting_without_login() throws JsonProcessingException {
            receiveLineWithTimeout(inUser1, "HI expected");

            // user1 tries to invite without logging in
            outUser1.println(Utils.objectToMessage(new ToHInviteReq("someone")));
            outUser1.flush();

            String response = receiveLineWithTimeout(inUser1, "TOH_RESP expected");
            ToHResp resp = Utils.messageToObject(response);
            assertEquals("ERROR", resp.status());
            assertEquals(67, resp.code());
        }
    }

    private String receiveLineWithTimeout(BufferedReader reader, String message) {
        return assertTimeoutPreemptively(ofMillis(pingTimeMsDeltaAllowed), reader::readLine, message);
    }
}
