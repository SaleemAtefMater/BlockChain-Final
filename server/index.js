var socketIO = require('socket.io'),
    http = require('http'),
    port = process.env.PORT || 8080,
    ip = process.env.IP || '192.168.1.171', //My IP address. I try to "127.0.0.1" but it the same => don't run
    server = http.createServer().listen(port, ip, function () {
        console.log("IP = ", ip);
        console.log("start socket successfully");
    });

io = socketIO.listen(server);
//io.set('match origin protocol', true);
io.set('origins', '*:*');

var users = [];
var messages = [];
var groupMessages = [];
var groupMembers = [];
var group = [];
var chain = [];


var run = function (socket) {

    socket.on("message", function (value) {
        const obj = JSON.stringify(value);
        const obj1 = JSON.parse(obj);
        // socket.to(socket.id).emit("newMessage",value)
        // obj1.destination_id
        messages.push(value);
        socket.broadcast.emit("newMessage", messages);

        console.log(obj1.message+" message");
        console.log(obj+" mmm");
    });

    socket.on("chain", function (value) {
        const obj = JSON.stringify(value);
        const obj1 = JSON.parse(obj);
        // socket.to(socket.id).emit("newMessage",value)
        // obj1.destination_id
        chain.push(value);
        socket.broadcast.emit("newChain", chain);

        console.log(obj1.message+" chain");
        console.log(obj+" chain");
    });

    socket.on("groupMessages", function (value) {
        const obj = JSON.stringify(value);
        const obj1 = JSON.parse(obj);
        groupMessages.push(value);
        socket.broadcast.emit("newGroupMessage", groupMessages);
        groupMessages.forEach(element => {
            console.log(element);
        });

        console.log(obj1.message+" message");
        console.log(obj+" ggg");
    });


    socket.on("create-group", function (value) {
        console.log(value.id + " create-group");
        const obj = JSON.stringify(value);
        const obj1 = JSON.parse(obj);
        console.log(obj1+" group")
        group.push(value);
    });
    socket.on("group-view", function (value) {
        socket.emit("group-name", group);
    });

    socket.on("user-join_group", function (value) {
        console.log(value.id + " user-join-group");
        const obj = JSON.stringify(value);
        const obj1 = JSON.parse(obj);
        console.log(obj1+" group")
        groupMembers.push(value);
        groupMembers.forEach((user) => {
            console.log(user.id+" groupMembers")
        });

    });
    socket.on("group-members_view", function (value) {
        socket.broadcast.emit("group-members_emit", groupMembers);
    });

    socket.on("leave_group", function (value) {
        groupMembers.forEach((user,index) => {
            console.log(groupMembers.length+" after user")
            if (user.id === value){
                groupMembers.splice(user,1)
            }
            console.log(groupMembers.length+" leve ddd")
        });
        socket.broadcast.emit("leave_group_emit", groupMembers);

    });


    socket.on("user-join", function (value) {
        console.log(value.id + " user-join");
        const obj = JSON.stringify(value);
        const obj1 = JSON.parse(obj);
       console.log(obj1)
        //console.log(obj)
        users.push(value);

    });
    socket.on("user-view", function (value) {
        // console.log(value + " user-join");
        socket.emit("new-users", users);
        users.forEach((user) => {
            console.log(user.id+"new-users")
        });

    });

    socket.on("user-isTyping", function (value) {
        const obj = JSON.stringify(value);
        const obj1 = JSON.parse(obj);
        users.forEach((user) => {
            if (user.id == obj1.id){
                user.typing = true
            }
            console.log(user.typing)
            console.log(value.id+" typing")
        });
        socket.broadcast.emit("user-isTyping_emit", value);
    });

    socket.on("disconnect_user", function (value) {
        const obj = JSON.stringify(value);
        const obj1 = JSON.parse(obj);
        users.forEach((user) => {
            if (user.id == obj1.id){
                user.status = false
            }
            console.log(user.status)
            console.log(obj1.id+" ddd id")
            console.log(user.id+" uuu id")
            console.log(value.id+" disconnect")
        });
        socket.broadcast.emit("disconnect_user_emit", value);

    });
}

io.sockets.on('connection', run);
