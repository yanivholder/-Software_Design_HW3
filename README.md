# TechWM • Assignment 3

## Authors
* Eilon Kornboim, 315677880
* Yaniv Holder, 207025297

## Notes


### Implementation Summary
- We added a wrapper to the chosen library to make it more user-friendly and less coupled. The wrapper is crucial becuase this library does not encapsulate SecureStorage as we want from the higher level library layer. so the wrapper hold a SecureStorage instance so we get an encapsulating library.
- We changed the External Library's Module since it requires binding to stuff like "loan manager" which are irrelevant.
- We removed irrelevant files from the library which are for previous assignements becuase they add uneeded API
- We picked this library (although all abobve changes were needed) because it works, while some others did not pass our tests.
- The message client is pretty straight forward

### Testing Summary
- Tested the logic of client messaging like we tested our logic in previous assignments, 
- Tested the chosen library by creating a wrapper to it that conforms to our API and ran all tests from assignments 1 & 2, so we know it's kosher.

### Difficulties
It was difficult to choose a library when some don't qualify to the assignment requirements and other are buggy, forcing us to pick a library we are not fully satisfied with, but works (guess this is a real life experience)

### Feedback
Interesting assignment, but I think it's not the right one for this phase of the course.
The main thing was using a foreign library, but we could have done this with a much smaller logic (client msg) and use the rest of the assignment for stuff we did not practice like design patterns, functional programing...
We did use a foreign library, but I don't think this justifies a while assignment from only 3 during the semester