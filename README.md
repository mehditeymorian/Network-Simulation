# Network-Simulation
This is a network simulation project written for the `Computer Networks Course`. `Link-State` protocol is used as network topology. Routers connect using `UDP` c
and connection between a Router and NetworkManager is based on `TCP` protocol. `Dijkstra algorithm` is used to update the forwarding table.

# How the Simulation Works
NetworkManager read a config file using NetworkConfig class and run **N** routers where **N** is routers count which is read from the config file. afterward, each router connects to NetworkManager.
Both NetworkManager and Router have request handlers for talking to each other. Next step, NetworkManager sends routers their connectivity table. routers check their connection with their neighbors.
At the end, routers become ready for routing, NetworkManager sends a network-ready signal to all routers. now, NetworkManager can take routing commands.

# Protocols
Protocol between NetworkManager and a Router
```
ACTION
/r/n
DATA
/r/n
/r/n
```
Protocol between Routers
```
ACTION\n
data\n
data\n
data\n
$end
```

Routing Protocol between NetworkManager and Router
```
ROUTING\r\n
START END\r\n
```
Routing Protocol between Routers
```
ROUTING\r\n
START END
START-TIME
PATH-ROUTER[0 1 2 3 4]
```

# Sample Simulation
Input
```
4
0 1 5
0 2 3
1 3 1
1 2 9
2 3 2
```

Output
```
[Jun 28,2021 08:44:07.390][Configuration] Starting Manager
[Jun 28,2021 08:44:07.436][Configuration] Reading Configuration...
[Jun 28,2021 08:44:07.442][Configuration] Configuration Read Successfully.
[Jun 28,2021 08:44:07.444][Manager] Listen on port 9000
[Jun 28,2021 08:44:07.447][Router 0 ] Started Working.
[Jun 28,2021 08:44:07.461][Router 1 ] Started Working.
[Jun 28,2021 08:44:07.462][Manager] UDP Port 9007 Received from Router 0.
[Jun 28,2021 08:44:07.462][Router 0 ] Connectivity Table Received. Neighbors-Ids: 1 2 .
[Jun 28,2021 08:44:07.464][Router 2 ] Started Working.
[Jun 28,2021 08:44:07.464][Manager] UDP Port 9008 Received from Router 1.
[Jun 28,2021 08:44:07.464][Router 1 ] Connectivity Table Received. Neighbors-Ids: 0 2 3 .
[Jun 28,2021 08:44:07.464][Manager] Received Ready Signal from router 0.
[Jun 28,2021 08:44:07.474][Router 3 ] Started Working.
[Jun 28,2021 08:44:07.475][Manager] Received Ready Signal from router 1.
[Jun 28,2021 08:44:07.476][Manager] UDP Port 9009 Received from Router 2.
[Jun 28,2021 08:44:07.476][Router 2 ] Connectivity Table Received. Neighbors-Ids: 0 1 3 .
[Jun 28,2021 08:44:07.478][Manager] Received Ready Signal from router 2.
[Jun 28,2021 08:44:07.478][Router 3 ] Connectivity Table Received. Neighbors-Ids: 1 2 .
[Jun 28,2021 08:44:07.478][Manager] UDP Port 9010 Received from Router 3.
[Jun 28,2021 08:44:07.480][Manager] Received Ready Signal from router 3.
[Jun 28,2021 08:44:07.481][Manager] Network is Safe.
[Jun 28,2021 08:44:07.482][Router 0 ] Received Safe Signal.
[Jun 28,2021 08:44:07.482][Router 3 ] Received Safe Signal.
[Jun 28,2021 08:44:07.482][Router 2 ] Received Safe Signal.
[Jun 28,2021 08:44:07.482][Router 1 ] Received Safe Signal.
[Jun 28,2021 08:44:07.485][Router 0 ] Received CHECK_CONNECTION Signal from Router 2.
[Jun 28,2021 08:44:07.485][Router 3 ] Received CHECK_CONNECTION Signal from Router 2.
[Jun 28,2021 08:44:07.485][Router 2 ] Received CHECK_CONNECTION Signal from Router 3.
[Jun 28,2021 08:44:07.485][Router 1 ] Received CHECK_CONNECTION Signal from Router 3.
[Jun 28,2021 08:44:07.487][Router 3 ] Received CHECK_CONNECTION Signal from Router 1.
[Jun 28,2021 08:44:07.487][Router 2 ] Received CHECK_CONNECTION Signal from Router 0.
[Jun 28,2021 08:44:07.489][Router 2 ] Received CHECK_CONNECTION Signal from Router 1.
[Jun 28,2021 08:44:07.489][Router 0 ] Received CHECK_CONNECTION Signal from Router 1.
[Jun 28,2021 08:44:07.489][Router 1 ] Received CHECK_CONNECTION Signal from Router 2.
[Jun 28,2021 08:44:07.490][Router 3 ] All ACKS Received from Neighbors.
[Jun 28,2021 08:44:07.491][Router 1 ] Received CHECK_CONNECTION Signal from Router 0.
[Jun 28,2021 08:44:07.491][Router 2 ] All ACKS Received from Neighbors.
[Jun 28,2021 08:44:07.492][Router 0 ] All ACKS Received from Neighbors.
[Jun 28,2021 08:44:07.492][Router 1 ] All ACKS Received from Neighbors.
[Jun 28,2021 08:44:07.493][Manager] Network is Ready.
[Jun 28,2021 08:44:07.495][Router 0 ] Received Network Ready Signal.
[Jun 28,2021 08:44:07.495][Router 3 ] Received Network Ready Signal.
[Jun 28,2021 08:44:07.495][Router 2 ] Received Network Ready Signal.
[Jun 28,2021 08:44:07.495][Router 1 ] Received Network Ready Signal.
[Jun 28,2021 08:44:07.508][Router 1 ] Receive LSP from Router 0.
[Jun 28,2021 08:44:07.508][Router 3 ] Receive LSP from Router 1.
[Jun 28,2021 08:44:07.508][Router 0 ] Receive LSP from Router 1.
[Jun 28,2021 08:44:07.508][Router 2 ] Receive LSP from Router 3.
[Jun 28,2021 08:44:07.510][Router 3 ] Forwarding Table Updated.
[Jun 28,2021 08:44:07.509][Router 1 ] Receive LSP from Router 3.
[Jun 28,2021 08:44:07.511][Router 2 ] Receive LSP from Router 0.
[Jun 28,2021 08:44:07.511][Router 0 ] Forwarding Table Updated.
[Jun 28,2021 08:44:07.512][Router 1 ] Forwarding Table Updated.
[Jun 28,2021 08:44:07.515][Router 2 ] Forwarding Table Updated.
[Jun 28,2021 08:44:07.523][Router 3 ] Receive LSP from Router 2.
[Jun 28,2021 08:44:07.523][Router 2 ] Receive LSP from Router 1.
[Jun 28,2021 08:44:07.523][Router 0 ] Receive LSP from Router 2.
[Jun 28,2021 08:44:07.523][Router 1 ] Receive LSP from Router 2.
[Jun 28,2021 08:44:10.695][Router 0 ] Received Routing Packet From Manager. Destination: Router 2.
[Jun 28,2021 08:44:10.695][Router 2 ] I Got my Packet :) From: 0, Took: 0ms, Path 0 2.
[Jun 28,2021 08:44:13.911][Router 2 ] Received Kill Request.
[Jun 28,2021 08:44:13.912][Router 2 ] Killing UDP Request Handler.
[Jun 28,2021 08:44:13.912][Router 2 ] Shutdown.
```
