#!/usr/bin/env bash
set -e

case $1 in
  master)
    IMAGE_TAG_NAME="latest"
    ;;
  develop)
    IMAGE_TAG_NAME="beta"
    ;;
  feature/openapi-v3)
    IMAGE_TAG_NAME="oas3"
    ;;
esac

echo "${IMAGE_TAG_NAME}"
