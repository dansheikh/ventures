# Setup

1. Install Google Java Code Style

    - Linux: 
    <pre><code>
    curl -#L -o ~/.&ltPRODUCT&gt&ltVERSION&gt/config/codestyles/GoogleStyle.xml

    https://raw.githubusercontent.com/google/styleguide/gh-pages/intellij-java-google-style.xml
    </code></pre>

    - Mac
    <pre><code>
    curl -#L -o ~/Library/Preferences/&ltPRODUCT&gt&ltVERSION&gt/config/codestyles/GoogleStyle.xml

    https://raw.githubusercontent.com/google/styleguide/gh-pages/intellij-java-google-style.xml
    </code></pre>    

2. Install [SonarLint](https://www.sonarlint.org/ "SonarLint")

3. Clone repository to desired location.

# Usage

Once the repository has been cloned to a desired destination, the application and live-reloading may be initiated by executing the following commands (from the root directory) in *two separate* terminals:

- `./gradlew build -x test --continuous`

- `./gradlew bootRun`

