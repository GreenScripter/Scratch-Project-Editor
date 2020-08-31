# Scratch-Project-Editor
A library for handling low level modification of scratch projects.
This library can read scratch sb2 and sb3 files, as well as raw project.json files used to define projects.
There are also some features for interacting with the scratch website itself, such as downloading projects.

Read zip/sb2/sb3 files using the FullProject class, which also stores assets, using `new FullProject(byte[] data)` or `new FullProject(File project)`
Read project.json files or raw project data using `Project.getProject(String json)`
The class `ProjectFetch` is a tool for downloading projects from the scratch website, as well as their author and view data from the api. This tool can download unshared projects, but not their view or author data.

This library should be able to read and export any project file without damaging it, but is fully capable of creating invalid project files. This is not the ideal tool for generating code, as it can be a bit messy, but it totally possible with some effort and helper methods. 
If you are looking to generate code, you should definitely make sure to compare your results to actual blocks and have a good understanding of the project format.

This library requires Gson to build.
