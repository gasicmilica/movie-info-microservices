#!/usr/bin/env bash

mkdir microservices
cd microservices

spring init \
--boot-version=3.2.0 \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=movie-service \
--package-name=se.magnus.microservices.core.movie \
--groupId=se.magnus.microservices.core.movie \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
--type=gradle-project \
movie-service

spring init \
--boot-version=3.2.0 \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=screening-service \
--package-name=se.magnus.microservices.core.screening \
--groupId=se.magnus.microservices.core.screening \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
--type=gradle-project \
screening-service

spring init \
--boot-version=3.2.0 \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=rating-service \
--package-name=se.magnus.microservices.core.rating \
--groupId=se.magnus.microservices.core.rating \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
--type=gradle-project \
rating-service

spring init \
--boot-version=3.2.0 \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=comment-service \
--package-name=se.magnus.microservices.core.comment \
--groupId=se.magnus.microservices.core.comment \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
--type=gradle-project \
comment-service

spring init \
--boot-version=3.2.0 \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=movie-composite-service \
--package-name=se.magnus.microservices.composite.movie \
--groupId=se.magnus.microservices.composite.movie \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
--type=gradle-project \
movie-composite-service
