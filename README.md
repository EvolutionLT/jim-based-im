## 字段描述
1. cmd:命令码19
2. fromUserId:发送用户id(次字段必须和userId一起使用获取双方聊天信息)，非必填
3. userId:当前用户id,只有此字段时type须为0，代表获取当前用户所有离线消息（好友+群组）
4. groupId:群组id非必填,此字段必须和userId一起使用，代表该用户在群组内的聊天消息
5. beginTime: 消息开始时间，Date毫秒数，非必填
6. endTime: 消息结束时间，Date毫秒数，非必填
7. offset: 分页偏移量，int,类似limit 0,10,中的0，非必填
8. count: 显示消息数量，类似limit 0,10中的10,非必填
9. type: 消息类型（0:离线消息，1:历史消息）


## 连接
```
ws://127.0.0.1:8889/?token=7a552681e17b4f6e95d52d9b06e3e1fb
```
token为应用服务认证后im服务生成的连接token

## 通信使用样例:
#### 1.消息发送
oqRgw5U2o6siu0viOo2MQJIBafcw向asdfghjkl发送消息
> 请求内容
```json
{
	"to":"oqRgw5U2o6siu0viOo2MQJIBafcw",
	"from":"asdfghjkl",
	"cmd":"11",
	"createTime":1568012384,
	"msgType":0,
	"chatType":2,
	"content":{"asd":"hell,你2好"}
}
```
> 返回结果
```json
{
  "code": 10000,
  "command": 12,
  "msg": "ok 发送成功"
}

```
> 消息接收方收到内容


-------------------------------------------------------------
#### 2.拉取某人对某人的离线消息
*并删除队列，此接口用于点击阅读未读消息使用*
> 请求内容
```json
{
  "cmd": 19,
  "type":"0",
  "userId":"asdfghjkl",
  "fromUserId":"oqRgw5U2o6siu0viOo2MQJIBafcw",
  "offset":1,
  "count":2
}
```
> 返回结果：
```json
{
  "code": 10018,
  "command": 20,
  "data": {
    "friends": {
      "asdfghjkl": [{
        "chatType": 2,
        "cmd": 11,
        "content": "{\"asd\":\"hell,你1好\"}",
        "createTime": 1568012384,
        "from": "asdfghjkl",
        "id": "2d225ad13d3c4c6e803baf3b46473310",
        "msgType": 0,
        "to": "oqRgw5U2o6siu0viOo2MQJIBafcw"
      }, {
        "chatType": 2,
        "cmd": 11,
        "content": "{\"asd\":\"hell,你1好\"}",
        "createTime": 1568012384,
        "from": "asdfghjkl",
        "id": "3b8f380e2b3a45579130735e9845a737",
        "msgType": 0,
        "to": "oqRgw5U2o6siu0viOo2MQJIBafcw"
      }, {
        "chatType": 2,
        "cmd": 11,
        "content": "{\"asd\":\"hell,你1好\"}",
        "createTime": 1568012384,
        "from": "asdfghjkl",
        "id": "7928fbc809b2458eb01cf7f6eac4e569",
        "msgType": 0,
        "to": "oqRgw5U2o6siu0viOo2MQJIBafcw"
      }]
    },
    "groups": {},
    "userid": "oqRgw5U2o6siu0viOo2MQJIBafcw"
  },
  "msg": "get user message ok! 获取历史消息成功!"
}
```
-------------------------------------------------------------

#### 3.拉取某人对某人的离线消息
*不删除队列，此接口可以用于统计未读消息*
> 请求内容
```json
{
  "cmd": 19,
  "type":"2",
  "userId":"asdfghjkl",
  "fromUserId":"oqRgw5U2o6siu0viOo2MQJIBafcw",
  "offset":1,
  "count":2
}
```
> 返回接口结果同上

-------------------------------------------------------------
#### 4.拉取对于某人的历史消息，后台服务存储的用户聊天记录，可作为辅助接口，消息内容可以前端存储
```json
{
  "cmd": 19,
  "type":"2",
  "userId":"asdfghjkl",
  "fromUserId":"oqRgw5U2o6siu0viOo2MQJIBafcw",
  "offset":1,
  "count":2
}
```
返回接口同上

-------------------------------------------------------------
#### 5.获取好友列表
> 请求内容
```json
{
  "cmd": 17,
  "type":"1",
  "userId":"oqRgw5U2o6siu0viOo2MQJIBafcw"
}
```
> 返回结果
```json
{
  "code": 10003,
  "command": 18,
  "data": [{
    "id": "asdfghjkl",
    "nick": "770e4",
    "terminal": "ws"
  }],
  "msg": "ok 获取用户信息成功!"
}
```
-------------------------------------------------------------

6.心跳包，用于防止服务器断开连接
> 请求内容
```json
{"cmd":13,"hbbyte":"-127"}
```
> 返回结果
```json
{
  "command": 13,
  "data": {
    "createTime": 1568380291479,
    "hbbyte": -128
  }
}
```

