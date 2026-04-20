const express = require('express');
const http = require('http');
const { Server } = require('socket.io');

const app = express();
const server = http.createServer(app);
const io = new Server(server, {
  cors: {
    origin: "*",
  }
});

app.get('/', (req, res) => {
  res.send('SpeakAid Socket Server is running');
});

io.on('connection', (socket) => {
  console.log('A user connected:', socket.id);

  // Join a specific room based on roomId (e.g., student ID or session ID)
  socket.on('joinRoom', (roomId) => {
    socket.join(roomId);
    console.log(`User ${socket.id} joined room: ${roomId}`);
  });

  // Listen for messages from a client
  socket.on('chatMessage', (data) => {
    const { room, text } = data;
    console.log(`Message to ${room}: ${text}`);
    
    // Broadcast to everyone in the room EXCEPT the sender
    socket.to(room).emit('receiveMessage', {
      text: text,
      senderId: socket.id
    });
  });

  socket.on('disconnect', () => {
    console.log('User disconnected');
  });
});

const PORT = process.env.PORT || 3000;
server.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});