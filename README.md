# Network-Simulation
This is a network simulation project written for the `Computer Networks Course`. `Link-State` protocol is used as network topology. Routers connect using `UDP` c
and connection between a Router and NetworkManager is based on `TCP` protocol. `Dijkstra algorithm` is used to update the forwarding table.

# How the Simulation Works
NetworkManager read a config file using NetworkConfig class and run **N** routers where **N** is routers count which is read from the config file. afterward, each router connects to NetworkManager.
Both NetworkManager and Router have request handlers for talking to each other. Next step, NetworkManager sends routers their connectivity table. routers check their connection with their neighbors.
At the end, routers become ready for routing, NetworkManager sends a network-ready signal to all routers. now, NetworkManager can take routing commands.

