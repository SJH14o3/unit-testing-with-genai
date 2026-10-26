# Unit Testing with GenAI
This is my bachelor's degree project.  
Artificial intelligence tools that have reached to a point that they have become practical.
My task is from the perspective of generating unit test cases,
how are they, which one is better, and how easy is it to work with.
I will also evaluate how prompts be given and what tasks can be done with the aim of generating unit tests.  
A particular interest is to figure out if local models are well-suited to handle generating unit tests.  
Integration testing is not a matter of interest, and it won't be approached in this project.  
The tools are an extension on IDEs or a program themselves.
These units themselves have different levels of complexity.  
I will choose three open source projects with different levels of complexity in units.

These are the tools that I will work with:
- GitHub Copilot
- Continue.dev
- OpenCode

## Projects for Unit Testing
Each project will have its own folder containing the tested units, generated unit tests, and a report.

### A java unit testing demo
For the small project, I felt it would be a good fit to choose a unit testing demo. here is the link to the repository:  
https://github.com/AntonisGkortzis/UnitTestingDemoProject  
This project has four units, with quite simple methods. It won't be hard to achieve even MC/DC.  
There is a folder containing the unit testing demos of the original project, just for quick access.

### Google Gson
For the medium project, Google Gson has been chosen. It's a Java library used to convert Java Objects into an equivalent JSON and vice versa. Here is a link to its repository:  
https://github.com/google/gson  
There is a massive leap between this project and the previous one. for comparison, the portion of the code that tested was about 80 LOC, but  for this project, that number is around 10000.
Consequently, the report on generated tests is not as thorough.

### Keycloak
This project has been chosen as the hard project.
It's a java identity and access management which
users authenticate with Keycloak rather than individual applications.
Here is a link to its repository:  
https://github.com/keycloak/keycloak
For this project, unit tests for the same amount of LOC as Gson will be written.
However, this is a multi-modular project with many dependencies.
This makes it much difficult to generate unit test for.

### ترجمه
از مدل های gemmatranslate  و MiniMax 3.1 Flash برای ترجمه استفاده شده است.
ممکن است ترجمه دقیق نباشد.