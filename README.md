im-system
===========================
Instant Messaging system implemented by Netty and Spring-boot

## Catalog
* ### im-system
    * [im-codec](#im-codec)
    * [im-common](#im-common)
    * [im-message-store](#im-message-store)
    * [im-service](#im-service)
    * [im-tcp-gateway](#im-tcp-gateway)

### [im-codec](./im-codec)
---

### [im-common](./im-common)
***

### [im-message-store](./im-message-store)
***

### [im-service](./im-service)
***

### [im-tcp-gateway](./im-tcp-gateway)
***

## Process of checking friendship
The request data: 
`{
     "appId" : 10000,
     "fromId" : "lld",
     "toIds" : ["lld2", "lld4", "lld5", "8888888"],
     "checkType" : 2
 }`
 
 **fromId:** the id for a current user 
 
 **toIds**: the friend id array of current user
 
 **checkType**: '1' means one-way verification, whereas '2' means two-way verification
 
#### Check friendship status (determined by requesting side)
**One-way verification**
1. '1' means that only checking if current user (fromId) has added the corresponding friend (toId)
2. '0' means that only checking if current user (fromId) didn't add the corresponding friend (toId)

**Two-way verification**
1. '1' means that two people (fromId and toId) are already friends
2. '2' means that current user (fromId) has added the corresponding friend (toId), but the corresponding friend didn't add the current user as a friend
3. '3' means that current user (fromId) didn't add the corresponding friend (toId), but this friend has added the current user as a friend
4. '4' means that two people (fromId and toId) are not friends

## Process of checking blacklist relationship
The request data: 
`{
     "appId" : 10000,
     "fromId" : "lld",
     "toIds" : ["lld2", "lld4", "lld5", "8888888"],
     "checkType" : 2
 }`
 
 **fromId:** the id for a current user 
 
 **toIds**: the friend id array of current user
 
 **checkType**: '1' means one-way verification, whereas '2' means two-way verification
 
#### Check blacklist status (determined by requesting side)
**One-way verification**
1. '1' means that only checking if current user (fromId) has blacked out the corresponding friend (toId)
2. '0' means that only checking if current user (fromId) didn't black out the corresponding friend (toId)

**Two-way verification**
1. '1' means that two people (fromId and toId) are not blacked out with each other
2. '2' means that current user (fromId) has blacked out corresponding friend (toId), but the corresponding friend didn't black out current user as a friend
3. '3' means that current user (fromId) didn't black out the corresponding friend (toId), but this friend has blacked out the current user as a friend
4. '4' means that two people (fromId and toId) are both blacked out with each other
