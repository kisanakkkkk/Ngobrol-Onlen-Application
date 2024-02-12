const net = require('net');

const clients = [];

const server = net.createServer((socket) => {
  console.log('Client connected:', socket.remoteAddress, socket.remotePort);

  clients.push(socket);
  
  socket.on('data', (data) => {
    const message = data.toString().trim();
    console.log('Received:', socket.remoteAddress, socket.remotePort, '-', message);

    // Broadcast the message to all connected clients except the sender
    clients.forEach((client) => {
      if (client !== socket) {
        client.write(`${message}\n`);
      }
    });
  });

  socket.on('end', () => {
    console.log('Client disconnected:', socket.remoteAddress, socket.remotePort);
    clients.splice(clients.indexOf(socket), 1);
  });
});

const PORT = 3001;
server.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}`);
});
