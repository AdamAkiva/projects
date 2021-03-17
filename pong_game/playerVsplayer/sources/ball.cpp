#include "../headers/ball.h"
#include "../headers/game.h"
#include "../headers/platform.h"

namespace AA
{

	Ball::Ball()  //constructor for Ball class (used mostly to initiate the values of the class)
	{

		x = Game::SCREEN_WIDTH / 2;
		y = Game::SCREEN_HEIGHT / 2;
		speed = 5;
	}

	void Ball::setBallDirections()  //a method to roll a random number for the direction between 1 to -1 (and little less then that but whatever)
	{

		//directionX = cos(M_PI*(rand() % 71) / 180);  //roll according to angles then convert to radians because cos is using radians
		//if (rand() % 2 == 0)
		//	directionX = -directionX;

		//directionY = sin(M_PI*(rand() % 65 + 25) / 180);  //roll according to angles then convert to radians because sin is using radians
		//if (rand() % 2 == 0)
		//	directionY = -directionY;

		while (1)
		{
			directionX = rand() % 3 - 1;
			if (directionX != 0) break;
		}
		
		while (1)
		{
			directionY = rand() % 3 - 1;
			if (directionY != 0)break;
		}


	}

	bool Ball::updateBallPosition(SDL_Renderer* renderer, SDL_Rect* ball, bool ballLaunched, SDL_Rect* plat1, SDL_Rect* plat2, int* p1Score, int* p2Score)  //a method to update the ball position the the screen
	{
		static int hits = 0;  //int to hold the number of hits because after 4 hits the speed increase

		if (ballLaunched == false)  //if its the run after the reset put the ball in default positions and roll and random direction
		{

			setBallDirections();
			ball->x = x;
			ball->y = y;
			ball->w = length;
			ball->h = length;

		}

		else if (ballLaunched == true)  //if its not the run after the reset
		{

			ball->x += directionX*speed;  //move the ball on the x axis

			if (ball->y <= 0 || ball->y + length >= Game::SCREEN_HEIGHT)  //if the ball reached the top or bottom of the screen flip the direction
				directionY = -directionY;

			ball->y += directionY*speed;  //move the ball on y axis


			if (ball->x <= Game::pWidth + Platform::LENGTH_FROM_BORDERS || ball->x + length >= Game::SCREEN_WIDTH - Platform::LENGTH_FROM_BORDERS)  //if the ball reacher the right and left borders of the screen
			{
				directionX = -directionX;  //flip the direction

				SDL_FlushEvents(SDL_FIRSTEVENT, SDL_LASTEVENT);

				if (ball->y + length >= plat1->y && ball->y <= plat1->y + Game::pHeight && ball->x <= Game::pWidth + Platform::LENGTH_FROM_BORDERS)  //if the ball hit platform1 increase player 1 score
				{
					(*p1Score)++;
					hits++;
				}

				else if (ball->y + length >= plat2->y && ball->y <= plat2->y + Game::pHeight && ball->x + length >= Game::SCREEN_WIDTH - Platform::LENGTH_FROM_BORDERS)  //if the ball hit platform 2 increase player 2 score
				{
					(*p2Score)++;
					hits++;
				}

				else  //if it didnt hit any platform
				{
					hits = 0;
					speed = 5;
					return true;
				}


				if (hits % 4 == 0)  //every 4 hits without misses increase the speed
				{
					speed++;
				}

			}	
		}
		return false;
	}
}