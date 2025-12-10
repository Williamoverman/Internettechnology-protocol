# Protocol description

This client-server protocol describes the following scenarios:
- Setting up a connection between client and server.
- Broadcasting a message to all connected clients.
- Periodically sending heartbeat to connected clients.
- Disconnection from the server.
- Handling invalid messages.
- List of connected clients.
- Private messages between 2 clients, receiving and sending.
- Tails or Heads game

In the description below, `C -> S` represents a message from the client `C` is sent to server `S`. When applicable, `C` is extended with a number to indicate a specific client, e.g., `C1`, `C2`, etc. The keyword `others` is used to indicate all other clients except for the client who made the request. Messages can contain a JSON body. Text shown between `<` and `>` are placeholders.

The protocol follows the formal JSON specification, RFC 8259, available on https://www.rfc-editor.org/rfc/rfc8259.html

All messages may end using Linux line endings (\n) or windows line endings (\r\n) and client and server should interpret both cases as valid messages.

# 1. Establishing a connection

The client first sets up a socket connection to which the server responds with a welcome message. The client supplies a username on which the server responds with an OK if the username is accepted or an ERROR with a number in case of an error.
_Note:_ A username may only consist of characters, numbers, and underscores ('_') and has a length between 3 and 14 characters.

## 1.1 Happy flow

Client sets up the connection with server.
```
S -> C: HI {"version": "<server version number>"}
```
- `<server version number>`: the semantic version number of the server.

After a while when the client logs the user in:
```
C -> S: LOGON {"username":"<username>"}
S -> C: LOGON_RESP {"status":"OK"}
```

- `<username>`: the username of the user that needs to be logged in.

To other clients (Only applicable when working on Level 2):
```
S -> others: JOINED {"username":"<username>"}
```

## 1.2 Unhappy flow
```
S -> C: LOGON_RESP {"status":"ERROR", "code":<error code>}
```      
Possible `<error code>`:

| Error code | Description                              |
|------------|------------------------------------------|
| 5000       | User with this name already exists       |
| 5001       | Username has an invalid format or length |      
| 5002       | Already logged in                        |

# 2. Broadcast message

Sends a message from a client to all other clients. The sending client does not receive the message itself but gets a confirmation that the message has been sent.

## 2.1 Happy flow

```
C -> S: BROADCAST_REQ {"message":"<message>"}
S -> C: BROADCAST_RESP {"status":"OK"}
```
- `<message>`: the message that must be sent.

Other clients receive the message as follows:
```
S -> others: BROADCAST {"username":"<username>","message":"<message>"}   
```   
- `<username>`: the username of the user that is sending the message.

## 2.2 Unhappy flow

```
S -> C: BROADCAST_RESP {"status": "ERROR", "code": <error code>}
```
Possible `<error code>`:

| Error code | Description              |
|------------|--------------------------|
| 67         | You need to be logged in |

# 3. Heartbeat message

Sends a ping message to the client to check whether the client is still active. The receiving client should respond with a pong message to confirm it is still active. If after 3 seconds no pong message has been received by the server, the connection to the client is closed. Before closing, the client is notified with a HANGUP message, with reason code 7000.

The server sends a ping message to a client every 10 seconds. The first ping message is send to the client 10 seconds after the client is logged in.

When the server receives a PONG message while it is not expecting one, a PONG_ERROR message will be returned.

## 3.1 Happy flow

```
S -> C: PING
C -> S: PONG
```     

## 3.2 Unhappy flow

```
S -> C: HANGUP {"reason": <reason code>}
[Server disconnects the client]
```      
Possible `<reason code>`:

| Reason code | Description      |
|-------------|------------------|
| 7000        | No pong received |    

```
S -> C: PONG_ERROR {"code": <error code>}
```
Possible `<error code>`:

| Error code | Description         |
|------------|---------------------|
| 8000       | Pong without ping   |    

# 4. Termination of the connection

When the connection needs to be terminated, the client sends a bye message. This will be answered (with a BYE_RESP message) after which the server will close the socket connection.

## 4.1 Happy flow
```
C -> S: BYE
S -> C: BYE_RESP {"status":"OK"}
[Server closes the socket connection]
```

Other, still connected clients, clients receive:
```
S -> others: LEFT {"username":"<username>"}
```

## 4.2 Unhappy flow

- None

# 5. Invalid message header

If the client sends an invalid message header (not defined above), the server replies with an unknown command message. The client remains connected.

Example:
```
C -> S: MSG This is an invalid message
S -> C: UNKNOWN_COMMAND
```

# 6. Invalid message body

If the client sends a valid message, but the body is not valid JSON, the server replies with a pars error message. The client remains connected.

Example:
```
C -> S: BROADCAST_REQ {"aaaa}
S -> C: PARSE_ERROR
```

# 7. List of connected clients.

The client asks for a list consisting of all existing clients connected to the server in that instance.

Example:
```
C -> S: ONLINE_REQ
S -> C: ONLINE {"usernames":["name1","name2","name3"]}
```

# 8. Private messages between 2 clients, receiving and sending.

Two clients can privately communicate with each other using the server.

## 8.1 Happy flow

Example:
```
C -> S: DM_REQ {"username":"<username>","message":"<message>"}
S -> C: DM_RESP {"status":"OK"}

Corresponding client receives:
S -> C: DM {"username":"<username>","message":"<message>"}
```

## 8.2 Unhappy flow

Example:
```
S -> C: DM_RESP {"status": "ERROR", "code": <error code>}
```
Possible `<error code>`:

| Error code | Description          |
|------------|----------------------|
| 9000       | User does not exist  |
| 9001       | Cannot DM yourself   |
| 67         | Need to be logged in |

## 9. Tails or Heads game

Two clients can play a Tails or Heads game with each other using the server.

## 9.1 Happy flow

Example:

- C1 = Speler A
- C2 = Speler B

```
C1 -> S: TOH_GAME {"opponent":"<username>"}  // Invite
S -> C1: TOH_GAME_RESP {"status":"OK"}

S -> C2: TOH_GAME {"type":"invite", "from":"<username>", "message":"Game invitation from <username>. Reply with TOH_GAME {"choice":"heads"} or {"choice":"tails"} to accept."}

C2 -> S: TOH_GAME {"choice":"tails"}  // Accept + choice
S -> C1: TOH_GAME {"type":"accepted", "message":"Opponent accepted. Make your choice."}
S -> C2: TOH_GAME {"type":"accepted", "message":"Game started. Your choice: tails"}

C1 -> S: TOH_GAME {"choice":"heads"}  // Round 1
// Server tosses coin (random: heads/tails)
S -> C1: TOH_GAME {"type":"result", "round":1, "coin":"heads", "winner":"C1", "score":{"C1":1,"C2":0}, "message":"Result: heads. You win the point (+1)."}
S -> C2: TOH_GAME {"type":"result", "round":1, "coin":"heads", "winner":"C1", "score":{"C1":1,"C2":0}, "message":"Result: heads. Opponent wins the point."}

// Bij tie (beide heads): S -> both: TOH_GAME {"type":"tie", "message":"Tie! Make another choice."}
// Bij mismatch: Server tosses en kent punt toe.

// Na 3 punten voor één speler (variabel aantal rounds):
S -> C1: TOH_GAME {"type":"end", "message":"You won the game!"}
S -> C2: TOH_GAME {"type":"end", "message":"You lost the game."}
```

## 9.2 Unhappy flow

Example:
```
S -> C: TOH_GAME_RESP {"status": "ERROR", "code": <error code>}
```
Possible `<error code>`:

| Error code  | Description                                            |
|-------------|--------------------------------------------------------|
| 10000       | User does not exist                                    |
| 10001       | Cannot play heads or tails with yourself               |
| 10002       | Cannot play game with someone who is already in a game |
| 10003       | User already has a pending invite                      |
| 10004       | Invalid choice                                         |