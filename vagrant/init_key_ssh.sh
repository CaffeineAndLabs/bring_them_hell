#!/usr/bin/env sh
# Generate SSH keys in order to play with bring-them-hell (Chaos Monkey)
# WARNING: Don't use it in production

[ -d ssh ] || mkdir ssh

# Generate a testing SSH key 
# / ! \ Don't use it in production
ssh-keygen -f ./ssh/bring-them-hell-test -N ""
