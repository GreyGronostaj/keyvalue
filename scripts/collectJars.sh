#!/usr/bin/env bash

mkdir jars
rm jars/*
cp runtime/* */runtime/* */build/libs/* jars 2> /dev/null || true
