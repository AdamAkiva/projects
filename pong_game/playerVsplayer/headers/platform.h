#ifndef __PLATFORM__
#define __PLATFORM__

#include <SDL.h>
#include <SDL_ttf.h>
#include <iostream>
#include <math.h>
#include "Ball.h"

namespace AA
{

	class Platform
	{

	private:
		int const y = 250; //const int to hold the y starting position of the platforms
		int speed;  //int to hold the speed of the platforms
		int directionP1, directionP2;  //int that is used for the algorithm to move the platforms in the right direction


	public:
		static int const LENGTH_FROM_BORDERS = 30;  //const int the can be accessed from anywhere that hold the space from the borders on the screen to where the platforms start

	public:
		Platform();  //constructor for the platform class (used mostly to initiate the vars in the class)

		void setDirectionP1(int directionP1){ this->directionP1 = directionP1; }  //method used in Game to change the direction of platform1 value according to the keypresses
		void setDirectionP2(int directionP2){ this->directionP2 = directionP2; }  //method used in Game to change the direction of platform2 value according to the keypresses

		void updatePlatformsPosition(SDL_Renderer* renderer, SDL_Rect* rect1, SDL_Rect* rect2, SDL_Rect* ball, bool gameStarted);  //method that is used to update the values of x and y of the platforms

	};

}

#endif