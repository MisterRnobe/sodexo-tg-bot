<br />
<p align="center">

  <h3 align="center">sodexo telegram bot</h3>

  <p align="center">
    Balance checker telegram bot for your Sodexo Restaurant Pass card
    <br />
    <br />
    <a href="http://t.me/sodexo_card_bot">Try the bot</a>
    ·
    <a href="https://github.com/MisterRnobe/sodexo-tg-bot/issues/new?assignees=&labels=bug&template=bug_report.md&title=">Report Bug</a>
    ·
    <a href="https://github.com/MisterRnobe/sodexo-tg-bot/issues/new?assignees=&labels=enhancement&template=feature_request.md&title=">Request Feature</a>
  </p>
</p>



## Table of Contents

* [About the Project](#about-the-project)
  * [Built With](#built-with)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Roadmap](#roadmap)
* [Contributing](#contributing)
* [License](#license)
* [Contact](#contact)



## About The Project

There are an amazing website and a mobile application to monitor sodexo card balance.
However, it does not support some features that would help me to keep track any balance changes, that's why I created the bot  


### Built With

* [quarkus](https://quarkus.io/)
* [mutiny](https://smallrye.io/smallrye-mutiny/)


## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

* Install and run mongo db
```sh
apt-get install mongo
```
or launch [docker container]() using
```shell script
docker run --name sodexo-tg-bot-mongo -d \
  -e MONGO_INITDB_ROOT_USERNAME=<INSERT USERNAME> \
  -e MONGO_INITDB_ROOT_PASSWORD=<INSERT PASSWORD> \
  -p 27017:27017 \
  mongo:4
```

* Create a free telegram bot at https://core.telegram.org/bots#3-how-do-i-create-a-bot and get its token

### Installation

1. Clone the repo
```sh
git clone https://github.com/MisterRnobe/sodexo-tg-bot.git
```
2. Run
```sh
./mvnw quarkus:dev \
    -DMONGO_URI="mongodb://<INSERT USERNAME>:<INSERT PASSWORD>@localhost:27017" \
    -DBOT_TOKEN="<INSERT YOUR BOT TOKEN>"
```



## Roadmap
See the [open issues](https://github.com/MisterRnobe/sodexo-tg-bot/issues) for a list of proposed features (and known issues).


## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request


## License

Distributed under the MIT License. See `LICENSE` for more information.


## Contact

Reach out to me at one of the following places

* [LinkedIn](https://www.linkedin.com/in/nikita-medvedev-643489190/)
