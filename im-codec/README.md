# im-codec
Customize transmission protocol for Instant Messaging system

## Design
**Request Header** `command, version, clientType, parseType, imeiLength, appId, bodyLength`

**imeiBody** imei content

**Request Body** Actual data content

### Request Header Description

**command:** Request type like GET, POST, PUT, DELETE, etc.

**version:** Transmission protocol version

**clientType:** Client type like IOS, Android, PC(Windows, Mac), Web, etc.

**parseType:** JSON, Protobuf.

**imeiLength:** The length of Imei number per client

**appId:** App id per each terminal as our System support multiple terminal access 

**bodyLength:** The length of body
