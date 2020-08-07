Scalable Music Server provides a list of music and binary songs via RESTful API

GET /lists
-returns a json array of songLists

GET /lists/id
-returns a json array of songs from the song list

GET /songs/id
-returns the song binary. supports partial content in the request header so the entire
binary does not need to be downloaded in one request.

The Application Server is implemented with the Play Framework using Scala.
The metadata is persisted in MySQL.
The song binaries can be stored either in the file system or in AWS S3.
In order for the music server to scale horizontally, I have a HAProxy load balancer
that sits in front of the application server and my node client resource server that serves the js, cs, and images.
Eventually would like to learn how serve the resource content from a CDN myself.
In order for the content to be resilient, I use S3 to save the song binaries and I
use MySQL to store the song information.
To reduce disk reads, I lazily cache the song binaries and music metatdata to reduce
the number times we need to talk to persisted store.

To populate the music library, there is a seperate Node js script that will write to
mySQL and update the songs and lists. I have currently manually uploaded the songs up
to AWS S3 but should really automate this as part of the process as well.

I have currently manually created the databsase schema in MySQL but plan to try to use
the evolutions tool within the play framework.