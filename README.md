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

## http接口
## 注册信息
```
http://127.0.0.1:8082/im/vanke/initUser
```
入参
```
{
//发送人userId
"senderId":"098f64aa0ac8ad53",
//接收人userId(可为空)
"receiverId":"",
//头像
"avatar":"ggggfff",
//昵称
"nick"："测试"
//终端
"terminal":"pc",
//登录token
"token": "b92cecbf47a4f04f3",
//身份类型,0=访客,1=客服, 2置业顾问
"type":0
}

```
返回值
```
{
    "code": 200,
    "msg": "success",
    "data": null
}
```

##注册信息，并且返回聊天对象信息
```
http://127.0.0.1:8082/im/vanke/getReceiver
```
入参
```
{
//发送人userId
"senderId":"098f64aa0ac8ad53",
//接收人userId,访客可为null
"receiverId":"",
"nick":"测试客服",
//头像
"avatar":"ggggfff",
//终端
"terminal":"pc",
//登录token
"token": "b92cecbf47a4f04f3",
//身份类型,0=访客,1=客服, 2置业顾问
"type":0
}

```
返回值
```
{
    "code": 200,
    "msg": "success",
    "data": {
        "id": "098f64aa0ac8ad55",
        "nick": "测试客服",
        "avatar": "ggggfff",
        "status": null,
        "sign": null,
        "terminal": "pc",
        "friends": null,
        "groups": null,
        "extras": null
    }
}
```


##维护聊天列表相关信息-所有联系人数量，待回复数量， 最近联系人数量， 某个对象未读消息数量
包含在线未读消息和离线未读消息
```
```
http://127.0.0.1:8082/im/vanke/chatInfo
```
入参
```
{
//接收人userId,访客可为null
"to":"098f64aa0ac8ad53",
//发送人userId
"from":"098f64aa0ac8ad53",
//操作 1=增加(有推送消息) 2删除(点击聊天列表进入聊天框)
"op": 1,
//是否是新成员 0=好友，1=新人
"isNewMember" :1,
//是否是当前聊天成员 0=不是 1=是
"isConcurrent"：1
}

```
返回值
```
{
    "code": 200,
    "msg": "success",
    "data": {
        //接收人userId,访客可为null
        "to":"098f64aa0ac8ad53",
        //发送人userId
        "from":"098f64aa0ac8ad53",
        //待回复数量
        "pendingReplyNum": 10,
        //最近联系人数量
        "lastedContactsNum":10,
        //所有人联系人数量
        "allContactsNum":10,
        //未读数量
        "unReadNum":10
    }
}

## 连接
```
ws://127.0.0.1:8889/?token=7a552681e17b4f6e95d52d9b06e3e1fb
```
token为应用服务认证后im服务生成的连接token

## 通信使用样例:
#### 1.消息发送
oqRgw5U2o6siu0viOo2MQJIBafcw向asdfghjkl发送消息
msgType: 0=text,1=沙盘连接， 2=文章连接
> 请求内容
```json
{
	"to":"oqRgw5U2o6siu0viOo2MQJIBafcw",
	"from":"asdfghjkl",
	"cmd":"11",
	"createTime":1568012384,
	"msgType":0,
	"chatType":2,
	"extras": {	
	        "nickName" : "昵称",
         	"headImg": "http://jjjj.img"
         	},
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
  "code": 200,
  "command": 18,
  "data": [{
    "id": "oqRgw5YLLbymI6SKG2Mqrpx-1Q0w",
    "nick": "栗子",
    "terminal": "ws"
  }, {
    "id": "oqRgw5U2o6siu0viOo2MQJIBafcw",
    "terminal": "ws"
  }],
  "msg": "success "
}
```
-------------------------------------------------------------

#### 6.心跳包，用于防止服务器断开连接
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

---
#### 7.拉取用户详细信息
包含用户好友、好友消息前10条、未读消息数量、好友的基本信息
pullType=1全部列表，2待回复列表，3最近两天联系人
> 请求内容
```json
{
  "cmd": 17,
  "type":"2",
  "userId":"asdfghjkl",
  "extras": {"pullType": 1,"searchKey": ""}
}
```
> 响应内容
```json
{
  "code": 200,
  "command": 18,
  "data": {
    "avatar": "",
    "friends": [{
      "avatar": "",
      "historyMessage": [{
        "chatType": 2,
        "cmd": 11,
        "content": "{\"asd\":\"hell,你2好\"}",
        "createTime": 1568557297820,
        "from": "oqRgw5YLLbymI6SKG2Mqrpx-1Q0w",
        "id": "0e848234998c443ebd5657ff83cc1d42",
        "msgType": 0,
        "to": "asdfghjkl"
      }],
      "nickname": "栗子",
      "unReadNum": 0,
      "userId": "oqRgw5YLLbymI6SKG2Mqrpx-1Q0w"
    }, {
      "avatar": "",
      "historyMessage": [{
        "chatType": 2,
        "cmd": 11,
        "content": "{\"asd\":\"hell,message from jkl\"}",
        "createTime": 1568012384,
        "from": "oqRgw5U2o6siu0viOo2MQJIBafcw",
        "id": "6094a267c0ff4814ab6e8114bf730778",
        "msgType": 0,
        "to": "asdfghjkl"
      }, {
        "chatType": 2,
        "cmd": 11,
        "content": "{\"asd\":\"hell,message from jkl\"}",
        "createTime": 1568012384,
        "from": "oqRgw5U2o6siu0viOo2MQJIBafcw",
        "id": "c3925e56024e43b79f2cd6bc8f218eff",
        "msgType": 0,
        "to": "asdfghjkl"
      }, {
        "chatType": 2,
        "cmd": 11,
        "content": "{\"asd\":\"hell,message from jkl\"}",
        "createTime": 1568012384,
        "from": "oqRgw5U2o6siu0viOo2MQJIBafcw",
        "id": "ed7ffc310af842c090ef3f61a7f186fb",
        "msgType": 0,
        "to": "asdfghjkl"
      }, {
        "chatType": 2,
        "cmd": 11,
        "content": "{\"asd\":\"hell,你2好\"}",
        "createTime": 1568012384,
        "from": "asdfghjkl",
        "id": "6cbf021e1d214c02b68cd1574dc98004",
        "msgType": 0,
        "to": "oqRgw5U2o6siu0viOo2MQJIBafcw"
      }, {
        "chatType": 2,
        "cmd": 11,
        "content": "{\"asd\":\"hell,你2好\"}",
        "createTime": 1568012384,
        "from": "asdfghjkl",
        "id": "d413c7a9b277460a858085178fcae65c",
        "msgType": 0,
        "to": "oqRgw5U2o6siu0viOo2MQJIBafcw"
      }, {
        "chatType": 2,
        "cmd": 11,
        "content": "{\"asd\":\"hell,message from fcw111\"}",
        "createTime": 1568556650564,
        "from": "oqRgw5U2o6siu0viOo2MQJIBafcw",
        "id": "6494f6b9b6f04d4a984c444fc4614410",
        "msgType": 0,
        "to": "asdfghjkl"
      }],
      "nickname": "",
      "unReadNum": 2,
      "userId": "oqRgw5U2o6siu0viOo2MQJIBafcw"
    }],
    "nickname": "770e4",
    "userId": "asdfghjkl"
  },
  "msg": "success "
}
```