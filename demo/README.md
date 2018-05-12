# Flaxo demo server

This module is used for mocking backend server. It can be usefull for 
front-end development or representation of the UI design.

Set system variable `REST_URL`. It will change client's endpoint to the fake backend.
```bash
export REST_URL=http://localhost:8081/api/flaxo/
```

First of all install http-fake-backend framework

```bash
git clone https://github.com/micromata/http-fake-backend.git
npm install
```

Add fake endpoints to fakeEndpoints folder and run

```bash
gradle moveEndpointToFakeBackend
( cd http-fake-backend/ && npm run start:dev )
```

or run single gradle task

```bash
gradle demoServer
```

*Notice:* Node demo server won't stop if you press `Ctrl+C` in your gradle demoServer task.
Therefore you should manually stop node processes.

Retrieve list of node applications

```bash
ps aux | grep node
```

To kill the process by the retrieved PID

```bash
kill -9 PID
```