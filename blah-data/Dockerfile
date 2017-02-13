FROM blah/rust

RUN apt-get install -y python

RUN mkdir /app
ADD . /app

WORKDIR /app

RUN cargo build --release

ENTRYPOINT ["target/release/blah-data"]

EXPOSE 8080
