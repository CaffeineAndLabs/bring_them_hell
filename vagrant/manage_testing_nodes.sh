#!/usr/bin/env sh

NODE_0="./i_want_hell_0"
NODE_1="./i_want_hell_1"
NODE_2="./i_want_hell_2"

_destroy_all_boxes () {
	local boxes
	boxes=$@

	for b in $boxes; do
		_destroy_vagrant_box "$b"
	done
}

_destroy_vagrant_box() {
	local guest_path
	guest_path=$1

	cd ${guest_path}
	vagrant destroy -f

	cd -
}

_up_all_boxes() {
	local boxes
	boxes="$@"

	for b in $boxes; do
		_up_vagrant_box "$b" &
	done
	wait
}

_up_vagrant_box() {
	local guest_path
	guest_path=$1

	cd ${guest_path}
	vagrant up

	cd -
}

_usage() {
	echo "Quick wrapper around vagrant CLI to play with testing nodes for bring-them-hell project"
	echo ""
	echo "./manage_testing_nodes.sh"
	echo "\t-h --help"
	echo "\t-s --start"
	echo "\t-d --destroy"	
	echo ""
}

[ -z $1 ] && _usage && exit 1

echo 
while [ "$1" != "" ]; do
	PARAM="$1"
	case $PARAM in
		-h | --help)
			_usage
			exit
			;;
		-s | --start)
			_up_all_boxes "$NODE_0" "$NODE_1" "$NODE_2"
			exit
			;;
		-d | --destroy)
			_destroy_all_boxes "$NODE_0" "$NODE_1" "$NODE_2" 
			exit
			;;
		*)
			echo "ERROR: unknown parameter '${PARAM}'"
			_usage
			exit 1
			;;
	esac
	shift
done
