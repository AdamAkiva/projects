#include "../headers/game.h"
#include "../headers/platform.h"

namespace AA
{

	Platform::Platform()  //constructor for the platform class (used mostly to initiate the vars in the class)
	{

		speed = 5;  //sets the speed for the platform movements
		directionP1 = 0;  //when you create platform object sets the direction of platform1 to 0 (no direction)
		directionP2 = 0;  //when you create platform object sets the direction of platform1 to 0 (no direction)

	}

	void Platform::updatePlatformsPosition(SDL_Renderer* renderer, SDL_Rect* rect1, SDL_Rect* rect2, SDL_Rect* ball, bool ballLaunched)  //checks if its the run after the reset of the program)  //method that is used to update the values of x and y of the platforms
	{
		Game game;

		if (ballLaunched == false)  //checks if its the first run after reset
		{

			rect1->x = LENGTH_FROM_BORDERS;
			rect1->y = y;
			rect1->w = Game::pWidth;
			rect1->h = Game::pHeight;

			rect2->x = Game::SCREEN_WIDTH - LENGTH_FROM_BORDERS;
			rect2->y = y;
			rect2->w = Game::pWidth;
			rect2->h = Game::pHeight;

			directionP1 = 0;
			directionP2 = 0;

		}

		else
		{

			if (rect1->y + speed*directionP1 > 0 && rect1->y + speed*directionP1 < Game::SCREEN_HEIGHT - rect1->h)  //algorithm to check if the platform1 reached the borders of the screen
				rect1->y += speed*directionP1;

			if (rect2->y + speed*directionP2 > 0 && rect2->y + speed*directionP2 < Game::SCREEN_HEIGHT - rect2->h)  //algorithm to check if the platform2 reached the borders of the screen
				rect2->y += speed*directionP2;

		}

	}
}