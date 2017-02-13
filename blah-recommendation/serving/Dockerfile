FROM blah/rust

RUN mkdir /app
ADD . /app

WORKDIR /app

RUN cargo build --release

ENTRYPOINT ["target/release/blah-reco"]

EXPOSE 8080
