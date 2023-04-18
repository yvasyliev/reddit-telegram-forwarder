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
   -DREDDIT_CLIENT_ID=${REDDIT_CLIENT_ID} \
   -DREDDIT_CLIENT_SECRET=${REDDIT_CLIENT_SECRET} \
   -DREDDIT_PASSWORD=${REDDIT_PASSWORD} \
   -DREDDIT_USERNAME=${REDDIT_USERNAME} \
   -DSUBREDDIT=${SUBREDDIT} \
   -jar reddit-telegram-repeater-${version}-jar-with-dependencies.jar
   ```
    The application relies on environment variables to extract subreddit data and repeat it to Telegram:

      |        Variable         | Required | Default value | Description                                                         | Example                                          |
      |:-----------------------:|:--------:|:-------------:|:--------------------------------------------------------------------|:-------------------------------------------------|
      |       `BOT_TOKEN`       |  `true`  |       -       | Telegram bot token                                                  | `4336854599:BBFqVLRq9ixVdxORFWQgaSywzCfRo5-tBus` |
      |     `BOT_USERNAME`      |  `true`  |       -       | Telegram bot username                                               | `SubredditResenderBot`                           |
      |      `CHANNEL_ID`       |  `true`  |       -       | Telegram channel ID. Subreddit posts will be sent here.             | `-1001572613876`                                 |
      |     `DEVELOPER_ID`      |  `true`  |       -       | Telegram profile ID. Bot will error logs messages directly to user. | `280538130`                                      |
      |   `REDDIT_CLIENT_ID`    |  `true`  |       -       | Reddit `client_id`                                                  | `pW134F0XNuueG4W78x9uGA`                         |
      | `DREDDIT_CLIENT_SECRET` |  `true`  |       -       | Reddit client `secret`                                              | `fsdT6VkTgf1WMfSW6Pd5t4DRvfVueB`                 |
      |   `DREDDIT_PASSWORD`    |  `true`  |       -       | Reddit profile password                                             | `ETD1fqx%cfk6odj#boj`                            |
      |   `DREDDIT_USERNAME`    |  `true`  |       -       | Reddit profile username                                             | `RedditProfileUsername000`                       |
      |       `SUBREDDIT`       |  `true`  |       -       | Subreddit name                                                      | `SubredditName`                                  |
      |     `SKIP_AUTHORS`      | `false`  |       -       | Post authors to be ignored; comma-separated values                  | `user1,user2,user3`                              |
