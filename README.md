# Red5 Javascript Bridge



## INTRODUCTION

A simple communication bridge between Red5 / Red5pro application and java script layer for RMI and EVENT based workflow. Thsi library is designed to help you connect your Red5 java application with javascript. It may be js running in the  browser or a node js application running on server.

You can invoke methods(RMI) in your main Application adapter class from javascript and receieve push notificatiosn from the application (EVENT). Data is exchanged between the two platforms in JSON format.

The bridge doe snto automatically expose all of the Red5 java api to javascript. You need to code the logic to do so yourself in your Application class, taking advantage of the RMI / EVENT capabilities of the bridge.
