# GS.HTTP
GS HTTP is a lightweight and fast utility designed for checking HTTP requests.
* [**GS.HTTP** on APKPure](https://apkpure.com/gs-http/com.flet.gshttp)
* [**GS.HTTP** on RuStore](https://www.rustore.ru/catalog/app/com.flet.gshttp)
* [**GS.HTTP** on RuMarket](https://ruplay.market)
* [**GS.HTTP** on GitHub -> Releases](https://github.com/proto-gs/GS.HTTP/releases/tag/v1.0.6)
# Information
GS HTTP is a lightweight and fast utility designed specifically for web developers, system administrators, and anyone working with APIs and network requests.

With GS HTTP, you can:

• Instantly check the HTTP status of any website (200 OK, 404 Not Found, etc.).

• Analyze server response headers.

• View and format JSON data in a convenient, readable format.

• Check cookies set by web resources.

The app is extremely minimalist: it doesn't require registration or collect personal data. Simply enter the website address and click "CHECK." All the necessary functionality is available immediately after launch. The perfect assistant for quickly diagnosing websites right from your smartphone.<br><br>
Using GS.HTTP, you can also:

• Control and manage application settings

• Use popular HTTP methods: GET, POST, HEAD, PUT

• Save everything locally in the request history
## Clone repository | Building app | Working with the project
There are two full-time IDLE programs where you can easily open and work with a project:
* [AndroidStudio](https://developer.android.com/studio)(recommended)
* [Intelij IDEA](https://www.jetbrains.com/idea/)<br><br>
Once you have downloaded IDLE from the official website, you can clone the repository using the command<br> `$ git clone https://github.com/proto-gs/GS.HTTP`<br><br>
To make changes to the repository, use the `git add` command. This will save all changes and stage them.<br> Commit with `git commit -m "Your description of the changes"` to commit the changes locally.<br> Push to GitHub with `git push` to upload files to the server.
Using the `git pull` command, you will pull in all updates from the GitHub repository, provided there are no conflicts in your local project.
For this you will also need git pre-installed.<br><br>
Once you have successfully cloned the repository, all you have to do is compile and run it using `./gradlew`<br>
To compile the Release version, use the command `./gradlew assembleRelease`<br> To compile the Debug version, use the command `./gradlew assembleDebug`<br>
To stop compilation, use the command `./gradlew --stop`<br> For help and all the options of `./gradlew` use the command `./gradlew --help`<br><br>

When choosing operating systems, it is recommended to choose Linux.
## License

`GS.HTTP` is licensed under the terms of the MIT License.

For more information, see [LICENSE](/LICENSE) file.

License of components and third-party dependencies it relies on might differ, check `LICENSE` file in the corresponding folder.
