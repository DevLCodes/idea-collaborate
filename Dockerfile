FROM ubuntu:latest
LABEL authors="lkumar"

ENTRYPOINT ["top", "-b"]