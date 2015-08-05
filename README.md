# stripsWiki


## Setup

1. `sbt > 13.7`
2. [mrmechko/flaming-tyrion](http://github.com/mrmechko/flaming-tyrion)
  Set the environment variable `tripsXMLBase=/path/to/flaming-tyrion/lexicon/data`
3. [mrmechko/TripsDiscussion](http://github.com/mrmechko/TripsDiscussion)
  Set the environment variable `tripsWikiBase=/path/to/TripsDiscussion/wiki
4. password-free push access to `TripsDiscussion`
  Either with an ssh key or [git-credential-store](http://git-scm.com/docs/git-credential-store).  `u/TripsBot` is an account explicitly for this purpose

## Run 

`sbt ~re-start`.  That's it.
  
  

