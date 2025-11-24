package protocoltests;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import org.opentest4j.AssertionFailedError;
import protocoltests.protocol.messages.*;
import protocoltests.protocol.utils.Utils;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Multi-user")
class MultiUser {

    private final static Properties PROPS = new Properties();

    private Socket socketUser1, socketUser2;
    private BufferedReader inUser1, inUser2;
    private PrintWriter outUser1, outUser2;
    private static String host;
    private static int port;
    private static int pingTimeMsDeltaAllowed;

    @BeforeAll
    static void setupAll() throws IOException {
        InputStream in = MultiUser.class.getResourceAsStream("testconfig.properties");
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

    @Nested
    @IndicativeSentencesGeneration(separator = " -> ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
    class A_JOINED_message_is_received_by_other_clients {

        @Test
        @Tag("RQ-U212")
        void when_a_user_connects() throws JsonProcessingException {
            receiveLineWithTimeout(inUser1, "Initial message expected");
            receiveLineWithTimeout(inUser2, "Initial message expected");

            // Connect user1
            outUser1.println(Utils.objectToMessage(new Logon("user1")));
            outUser1.flush();
            receiveLineWithTimeout(inUser1, "OK expected");

            // Connect user2
            outUser2.println(Utils.objectToMessage(new Logon("user2")));
            outUser2.flush();
            receiveLineWithTimeout(inUser2, "OK expected");

            //JOINED is received by user1 when user2 connects
            /* This test is expected to fail with the given NodeJS server because the JOINED is not implemented.
             * Make sure the test works when implementing your own server in Java
             */
            String res = receiveLineWithTimeout(inUser1, "JOINED expected");
            Joined joined = Utils.messageToObject(res);

            assertEquals(new Joined("user2"), joined);
        }
    }

    @Nested
    @IndicativeSentencesGeneration(separator = " -> ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
    class A_BROADCAST_is_received_by_all_other_users {

        @Test
        @Tag("RQ-U101")
        void when_a_user_sends_one() throws JsonProcessingException {
            receiveLineWithTimeout(inUser1, "Initial message expected");
            receiveLineWithTimeout(inUser2, "Initial message expected");

            // Connect user1
            outUser1.println(Utils.objectToMessage(new Logon("user1")));
            outUser1.flush();
            receiveLineWithTimeout(inUser1, "OK expected");

            // Connect user2
            outUser2.println(Utils.objectToMessage(new Logon("user2")));
            outUser2.flush();
            receiveLineWithTimeout(inUser2, "OK expected");
            /* This test is expected to fail with the given NodeJS server because the JOINED is not implemented.
             * Make sure the test works when implementing your own server in Java
             */
            receiveLineWithTimeout(inUser1, "JOINED expected");

            //send BROADCAST from user 1
            outUser1.println(Utils.objectToMessage(new BroadcastReq("messagefromuser1")));

            outUser1.flush();
            String fromUser1 = receiveLineWithTimeout(inUser1, "BROADCAST_RESP expected");
            BroadcastResp broadcastResp1 = Utils.messageToObject(fromUser1);

            assertEquals("OK", broadcastResp1.status());

            String fromUser2 = receiveLineWithTimeout(inUser2, "BROADCAST expected");
            Broadcast broadcast2 = Utils.messageToObject(fromUser2);

            assertEquals(new Broadcast("user1", "messagefromuser1"), broadcast2);

            //send BROADCAST from user 2
            outUser2.println(Utils.objectToMessage(new BroadcastReq("messagefromuser2")));
            outUser2.flush();
            fromUser2 = receiveLineWithTimeout(inUser2, "BROADCAST_RESP expected");
            BroadcastResp broadcastResp2 = Utils.messageToObject(fromUser2);
            assertEquals("OK", broadcastResp2.status());

            fromUser1 = receiveLineWithTimeout(inUser1, "BROADCAST expected");
            Broadcast broadcast1 = Utils.messageToObject(fromUser1);

            assertEquals(new Broadcast("user2", "messagefromuser2"), broadcast1);
        }

    }

    @Nested
    @IndicativeSentencesGeneration(separator = " -> ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
    class An_ERROR_message_is_received {

        @Test
        @Tag("RQ-U100")
        void when_trying_to_login_with_an_already_logged_in_username() throws JsonProcessingException {
            receiveLineWithTimeout(inUser1, "Initial message expected");
            receiveLineWithTimeout(inUser2, "Initial message expected");

            // Connect user 1
            outUser1.println(Utils.objectToMessage(new Logon("user1")));
            outUser1.flush();
            receiveLineWithTimeout(inUser1, "OK expected");

            // Connect using same username
            outUser2.println(Utils.objectToMessage(new Logon("user1")));
            outUser2.flush();
            String resUser2 = receiveLineWithTimeout(inUser2, "LOGON_RESP expected");
            LogonResp logonResp = Utils.messageToObject(resUser2);
            assertEquals(new LogonResp("ERROR", 5000), logonResp);
        }
    }

    private String receiveLineWithTimeout(BufferedReader reader, String message) {
        return assertTimeoutPreemptively(ofMillis(pingTimeMsDeltaAllowed), reader::readLine, message);
    }

}