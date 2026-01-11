# syntax=docker/dockerfile:1.5
ARG NODE_VERSION=20
FROM node:${NODE_VERSION}-alpine

ARG NPM_CLI_VERSION=latest

WORKDIR /workspace

# Keep npm updated and ready for project installs
RUN npm install -g npm@${NPM_CLI_VERSION}
