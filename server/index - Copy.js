var socketIO = require('socket.io'),
    http = require('http'),
    port = process.env.PORT || 5000,
    ip = process.env.IP || '192.168.1.231', //My IP address. I try to "127.0.0.1" but it the same => don't run
    server = http.createServer().listen(port, ip, function () {
        console.log("IP = ", ip);
        console.log("start socket successfully");
    });

io = socketIO.listen(server);
//io.set('match origin protocol', true);
io.set('origins', '*:*');

var users = [];
var me = [];
var run = function (socket) {


    socket.on("message", function (value) {
        const obj = JSON.stringify(value);
        const obj1 = JSON.parse(obj);
        //socket.to(socket.id).emit("newMessage",value)
        //obj1.destination_id

        socket.broadcast.emit("newMessage", value);
        console.log(obj1.message+" message");
        console.log(obj+" ssssss");
    });

    socket.on("user-join", function (value) {
        console.log(value + " user-join");
        const obj = JSON.stringify(value);
        const obj1 = JSON.parse(obj);
        console.log(obj1.image)
        console.log(obj)
        users.push(value);
    });
    socket.on("user-view", function (value) {
        console.log(value + " user-join");
        socket.broadcast.emit("new-users", users);
        console.log(users+"new-users")
    });

}

io.sockets.on('connection', run);