const stompClient = new StompJs.Client({
  brokerURL: 'ws://localhost:9000/ws-connect'
});

stompClient.onConnect = (frame) => {
  setConnected(true);
  console.log('Connected: ' + frame);

  stompClient.subscribe('/subscribe/chat.' + $("#chatroomId").val(), (message) => {
    let body = JSON.parse(message.body);
    let username = body.username;
    let content = body.content;
    showChat(username + ": " + content);
  });
};

function sendChat() {
  stompClient.publish({
    destination: "/publish/chat." + $("#chatroomId").val(),
    body: JSON.stringify({'username': $("#na").val(), 'content': $("#name").val()})
  });
  document.getElementById('name').value = '';
}

stompClient.onWebSocketError = (error) => {
  console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
  console.error('Broker reported error: ' + frame.headers['message']);
  console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  if (connected) {
    $("#conversation").show();
  }
  else {
    $("#conversation").hide();
  }
  $("#greetings").html("");
}

function connect() {
  stompClient.activate();
}

function disconnect() {
  stompClient.deactivate();
  setConnected(false);
  console.log("Disconnected");
}

function showChat(message) {
  // $("#greetings").append("<tr><td>" + message + "</td></tr>");


  // 메시지를 테이블에 추가
  const row = `<tr><td>${message}</td></tr>`;
  $("#greetings").append(row);

  // 스크롤을 최신 메시지로 이동
  const conversationDiv = document.getElementById("conversation");
  conversationDiv.scrollTop = conversationDiv.scrollHeight;
}

$(function () {
  $("form").on('submit', (e) => e.preventDefault());
  $( "#connect" ).click(() => connect());
  $( "#disconnect" ).click(() => disconnect());
  $( "#send" ).click(() => sendChat());
});
