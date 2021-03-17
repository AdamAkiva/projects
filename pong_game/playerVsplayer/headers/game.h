#ifndef __GAME__
#define __GAME__

#include <SDL.h>
#include <SDL_ttf.h>
#include "Platform.h"
#include "Ball.h"
#include <iostream>
#include <sstream>
#include <Windows.h>

namespace AA
{

	class Game
	{
	private:
		SDL_Window* window;  //create a window
		SDL_Renderer* renderer;  //set the renderer
		SDL_Texture* texture;  //set the texture
		SDL_Event event;  //for the events (mostly keypress events)
		SDL_Rect plat1, plat2, ball;  //rects for the platforms and the ball
		SDL_Surface* surface;  //surface for the texts
		SDL_Texture* winnerText, *player1ScoreText, *player2ScoreText;  //textures for the rects of texts
		SDL_Rect winner, player1Score, player2Score;  //rects for the texts
		TTF_Font* font;  //used to hold the font for the text

		bool ballLaunched;  //checks if its the run after the reset of the program
		bool p1Up, p1Down, p2Up, p2Down;  //bools for the keypresses of the players
		int lastTime;  //checks the of the processor since the last time (used to limit the fps to 60)

		Platform platform;  //platform object to use the functions of Platform
		Ball b;  //ball object to use the functions of Ball

	public:
		static const int  SCREEN_WIDTH = 800;  //const width for the screen that can be accessed from anywhere in the program
		static const int SCREEN_HEIGHT = 600;  //const height for the screen that can be accessed from anywhere in the program
		static const int pWidth = 10;  //const platform width that can be accessed from anywhere in the program
		static const int pHeight = 90; //const platform height that can be accessed from anywhere in the program 

		const int FPS = 60;  //int to set the limit of fps for the program
		const int FRAMES_PER_SECOND = 1000 / FPS;  //int to calc the miliseconds for the limit (using the FPS)

		bool reset;  //bool var to check if no one scored

		int p1Score;  //player 1 score
		int p2Score;  //player 2 score


	public:
		Game();  //constructor for Game (mostly used to initiate the vars in the class)
		void init();  //start SDL and SDL_ttf
		void buildScreen();  //sets the pointers of window,renderer,texture
		void buildGame();  //update the screen and pieces values
		bool startGame();  //start the game
		void drawPieces();  //draw the pieces on the screen
		void DrawScores();  //Draw the scores of the player
		void startScreen();  //Draw the start screen
		void drawText();  //draw the match score on the screen
		void freeObjects();  //free all the objects used by the program

	};

}

#endif