#include "../headers/game.h"
#include "../headers/platform.h"
#include "../headers/ball.h"
#include "time.h"
#include <iostream>

using namespace AA;

int main(int argc, char* args[])
{
	srand(time(NULL));

	Game pong;
	pong.startGame();
	pong.freeObjects();

	return 0;

}