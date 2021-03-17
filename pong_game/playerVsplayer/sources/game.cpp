#include "../headers/ball.h"
#include "../headers/game.h"

namespace AA
{

	Game::Game()  //constructor for Game (mostly used to initiate the vars in the class)
	{

		window = NULL;
		renderer = NULL;
		texture = NULL;
		surface = NULL;
		winnerText = NULL;

		p1Up = false;
		p1Down = false;
		p2Up = false;
		p2Down = false;

		ballLaunched = false;
		reset = false;

		lastTime = 0;

		p1Score = 0;
		p2Score = 0;


	}

	void Game::init()  //start SDL and SDL_ttf
	{

		if (SDL_Init(SDL_INIT_VIDEO) != 0)
			exit(1);

		if (TTF_Init() != 0)
			exit(1);

	}

	void Game::buildScreen()  //sets the pointers of window,renderer,texture
	{

		window = SDL_CreateWindow("Pong", SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, SCREEN_WIDTH, SCREEN_HEIGHT, NULL);
		if (window == NULL)
			exit(1);

		renderer = SDL_CreateRenderer(window, -1, SDL_RENDERER_PRESENTVSYNC);
		if (renderer == NULL)
			exit(1);

		texture = SDL_CreateTexture(renderer, SDL_PIXELFORMAT_ARGB8888, SDL_TEXTUREACCESS_TARGET, SCREEN_WIDTH, SCREEN_HEIGHT);
		if (texture == NULL)
			exit(1);

	}

	void Game::buildGame()  //update the screen and pieces values
	{
		if (ballLaunched==false)
		{
			SDL_SetRenderDrawColor(renderer, 0, 0, 0, 255);  //paint the screen black
			SDL_RenderClear(renderer);

			SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);  //sets the color to white in order to paint the platforms and the ball white

			platform.updatePlatformsPosition(renderer, &plat1, &plat2, &ball, ballLaunched);  //change the values of the platforms
			reset = b.updateBallPosition(renderer, &ball, ballLaunched, &plat1, &plat2, &p1Score, &p2Score);  //change the values of the ball and checks if no one scored by the reset var		

			drawPieces();  //draw the pieces on the screen
			DrawScores();  //draw each player score

			SDL_RenderPresent(renderer);  //present the new screen


		}
		else  //if its the first run
		{

			SDL_SetRenderDrawColor(renderer, 0, 0, 0, 255);  //paint the screen black
			SDL_RenderClear(renderer);

			SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);  //sets the color to white in order to paint the platforms and the ball white

			platform.updatePlatformsPosition(renderer, &plat1, &plat2, &ball, ballLaunched);  //change the values of the platforms
			reset = b.updateBallPosition(renderer, &ball, ballLaunched, &plat1, &plat2, &p1Score, &p2Score);  //change the values of the ball and checks if no one scored by the reset var

			if (reset == true)
			{

				SDL_SetRenderDrawColor(renderer, 0, 0, 0, 255);  //paint the screen black
				SDL_RenderClear(renderer);

				SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);  //sets the color to white in order to paint the platforms and the ball white

				drawText();

				reset = false;
				ballLaunched = false;

				p1Score = 0;
				p2Score = 0;

			}


			drawPieces(); //draw the pieces on the screen
			DrawScores();  //draw each player score

			SDL_RenderPresent(renderer);  //present the new screen

		}

	}


	void Game::drawPieces()  //draw the pieces on the screen
	{

		SDL_RenderDrawRect(renderer, &plat1);  //draw platform1
		SDL_RenderFillRect(renderer, &plat1);  //fill platform1 with the color white

		SDL_RenderDrawRect(renderer, &plat2);  //draw platform2
		SDL_RenderFillRect(renderer, &plat2);  //fill platform2 with the color white

		SDL_RenderDrawRect(renderer, &ball);  //draw the ball
		SDL_RenderFillRect(renderer, &ball);  //fill the ball with the color white

	}


	void Game::DrawScores()    //Draw the scores of the player
	{

		font = TTF_OpenFont("font.ttf", 14);
		SDL_Color color = { 255, 255, 255 };

		std::string temp = std::to_string(p1Score);
		char const* player1Text = temp.c_str();

		surface = TTF_RenderText_Solid(font, player1Text, color);
		player1ScoreText = SDL_CreateTextureFromSurface(renderer, surface);

		player1Score.w = surface->w;
		player1Score.h = surface->h;
		player1Score.x = 10;
		player1Score.y = 10;

		std::string temp2 = std::to_string(p2Score);
		char const* player2Text = temp2.c_str();

		surface = TTF_RenderText_Solid(font, player2Text, color);
		player2ScoreText = SDL_CreateTextureFromSurface(renderer, surface);

		player2Score.w = surface->w;
		player2Score.h = surface->h;
		player2Score.x = SCREEN_WIDTH-20;
		player2Score.y = 10;

		SDL_RenderCopy(renderer, player1ScoreText, NULL, &player1Score);
		SDL_RenderCopy(renderer, player2ScoreText, NULL, &player2Score);

	}

	void Game::drawText()  //draw the text of who won
	{
		while (true)
		{
			while (SDL_PollEvent(&event))
			{
				font = TTF_OpenFont("font.ttf", 46);
				SDL_Color color = { 255, 255, 255 };

				if (p1Score > p2Score)
				{
					surface = TTF_RenderText_Solid(font, "Player 1 wins!!!", color);

					winnerText = SDL_CreateTextureFromSurface(renderer, surface);
				}

				else if (p1Score < p2Score)
				{
					surface = TTF_RenderText_Solid(font, "Computer wins!!!", color);

					winnerText = SDL_CreateTextureFromSurface(renderer, surface);

				}

				else
				{
					surface = TTF_RenderText_Solid(font, "Draw!!!", color);

					winnerText = SDL_CreateTextureFromSurface(renderer, surface);

				}

				winner.h = surface->h;
				winner.w = surface->w;
				winner.x = 300;
				winner.y = 250;

				SDL_RenderCopy(renderer, winnerText, NULL, &winner);

				SDL_RenderPresent(renderer);

				if (event.type == SDL_KEYUP)
				{
					if (event.key.keysym.sym == SDLK_SPACE)
					{
						return;
					}
					if (event.key.keysym.sym == SDLK_ESCAPE)
					{
						SDL_QUIT;
						freeObjects();
						exit(0);
					}
				}
			}
		}

	}

	void Game::startScreen()  //Draw the start screen
	{
		while (true)
		{
			while (SDL_PollEvent(&event))
			{
				font = TTF_OpenFont("font.ttf", 24);
				SDL_Color color = { 255, 255, 255 };

				surface = TTF_RenderText_Solid(font, "Created by Adam Akiva press ENTER to start", color);

				winnerText = SDL_CreateTextureFromSurface(renderer, surface);

				winner.h = surface->h;
				winner.w = surface->w;
				winner.x = SCREEN_WIDTH / 4;
				winner.y = 280;

				SDL_RenderCopy(renderer, winnerText, NULL, &winner);

				SDL_RenderPresent(renderer);

				if (event.type == SDL_KEYUP)
				{
					if (event.key.keysym.sym == SDLK_RETURN)
					{
						return;
					}
					if (event.key.keysym.sym == SDLK_ESCAPE)
					{
						SDL_QUIT;
						freeObjects();
						exit(0);
					}
				}
			}
		}

	}

	bool Game::startGame()  //start the game
	{

		init();

		buildScreen();
		
		startScreen();

		SDL_FlushEvents(SDL_FIRSTEVENT, SDL_LASTEVENT);

		while (true)  //the game loop
		{
			int elapsed = SDL_GetTicks();  //var to hold the amount of ticks since SDL was initialized

			int interval = elapsed - lastTime;  //var to hold the time of each run

			while (SDL_PollEvent(&event))  //checks for an event
			{

				if (ballLaunched == false)
				{

					if (event.type == SDL_KEYUP)
					{
						if (event.key.keysym.sym == SDLK_SPACE)
						{
							ballLaunched = true;
						}
					}

				}

				if (event.type == SDL_KEYDOWN && ballLaunched==true)  //on keydown press move the platform accordingly
				{
					if (event.key.keysym.sym == SDLK_w)
					{
						platform.setDirectionP1(-1);
						p1Up = true;
					}
					if (event.key.keysym.sym == SDLK_s)
					{
						platform.setDirectionP1(1);
						p1Down = true;
					}
					if (event.key.keysym.sym == SDLK_UP)
					{
						platform.setDirectionP2(-1);
						p2Up = true;
					}
					if (event.key.keysym.sym == SDLK_DOWN)
					{
						platform.setDirectionP2(1);
						p2Down = true;
					}
				}

				if (event.type == SDL_KEYUP && ballLaunched == true)  //on keyup reset the bool vars to get the smooth feeling while playing
				{
					if (event.key.keysym.sym == SDLK_w)
					{
						platform.setDirectionP1(0);
						p1Up = false;
					}
					if (event.key.keysym.sym == SDLK_s)
					{
						platform.setDirectionP1(0);
						p1Down = false;
					}
					if (event.key.keysym.sym == SDLK_UP)
					{
						platform.setDirectionP2(0);
						p2Up = false;
					}
					if (event.key.keysym.sym == SDLK_DOWN)
					{
						platform.setDirectionP2(0);
						p2Down = false;
					}

				}

				if (event.type==SDL_KEYUP)
					if (event.key.keysym.sym == SDLK_ESCAPE)
						return false;

				if (event.type == SDL_QUIT)  //an event to quit the loop
					return false;
			}

			if (interval < FRAMES_PER_SECOND) SDL_Delay(FRAMES_PER_SECOND - interval);  //if the loop time is above the fps limit then delay the program

			buildGame();

			lastTime = elapsed;  //"reset the ticks"

		}
		return false;
	}

	void Game::freeObjects()  //free all the objects used by the program
	{

		SDL_DestroyTexture(texture);
		SDL_DestroyRenderer(renderer);
		SDL_DestroyWindow(window);
		SDL_UnlockSurface(surface);

	}



}