# Reddit-Telegram Repeater

Repeats Subreddit posts to Telegram.

# Requirements

- `Java 17`
- `Maven`
- Reddit profile
- Telegram profile
- Telegram channel

# Quickstart

1. Go to https://www.reddit.com/prefs/apps and create an app.
    * Application type must be `script`.
2. Build this project:
    ```shell
    git clone https://github.com/yvasyliev/AnadeArmasFunclub.git
    mvn clean package
    cd target
    ```
3. Run the app:
   ```shell
   java \
   -DBOT_TOKEN=${BOT_TOKEN} \
   -DBOT_USERNAME=${BOT_USERNAME} \
   -DCHANNEL_ID=${CHANNEL_ID} \
   -DDEVELOPER_ID=${DEVELOPER_ID} \
   -DPREVIOUS_REDDIT_POST_CREATED=${PREVIOUS_REDDIT_POST_CREATED} \
   -DREDDIT_CLIENT_ID=${REDDIT_CLIENT_ID} \
   -DREDDIT_CLIENT_SECRET=${REDDIT_CLIENT_SECRET} \
   -DREDDIT_PASSWORD=${REDDIT_PASSWORD} \
   -DREDDIT_USERNAME=${REDDIT_USERNAME} \
   -DSUBREDDIT=${SUBREDDIT} \
   -jar reddit-telegram-repeater-${version}-jar-with-dependencies.jar
   ```
    * The application relies on environment variables to extract subreddit data and repeat it to Telegram:

      <table>
         <tr>
            <th>Variable</th>
            <th>Required</th>
            <th>Default value</th>
            <th>Description</th>
            <th>Example</th>
         </tr>
         <tr>
            <td>`BOT_TOKEN`</td>
         </tr>
      </table>
      |            Variable            | Required | Default value | Description                                                         |
      |:------------------------------:|:--------:|:-------------:|:--------------------------------------------------------------------|
      |          `BOT_TOKEN`           |  `true`  |       -       | Telegram bot token                                                  |
      |         `BOT_USERNAME`         |  `true`  |       -       | Telegram bot username                                               |
      |          `CHANNEL_ID`          |  `true`  |       -       | Telegram channel ID. Subreddit posts will be sent here.             |
      |         `DEVELOPER_ID`         |  `true`  |       -       | Telegram profile ID. Bot will error logs messages directly to user. |
      | `PREVIOUS_REDDIT_POST_CREATED` | `false`  |       0       | Created time of last subreddit post.                                |
      |       `REDDIT_CLIENT_ID`       |     |     |     |
