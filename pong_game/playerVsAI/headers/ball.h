#ifndef __BALL__
#define __BALL__

#include <SDL.h>
#include <SDL_ttf.h>

namespace AA
{

	class Ball
	{

	private:
		int x, y;  //ints to hold the position of the ball on the x and y axis
		int speed;  //int to hold the speed of the ball movment
		float directionX, directionY;  //ints to to hold the direction on the x and y axis

	public:
		static const int length = 10;  //static const int that can be accessed from anywhere to hold the width and height of the ball

	public:
		Ball();  //constructor for Ball class (used mostly to initiate the values of the class)
		void setBallDirections();  //a method to roll a random number for the direction between 1 to -1 (and little less then that but whatever)
		bool updateBallPosition(SDL_Renderer* renderer, SDL_Rect* ball, bool ballLaunched, SDL_Rect* plat1, SDL_Rect* plat2, int* p1Score, int* p2Score, int* computerSpeed);  //a method to update the ball position the the screen

	};

}

#endif