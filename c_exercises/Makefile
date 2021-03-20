CC=gcc -g
EXEC=main
DEBUG = -g
CFLAGS = -std=c99  -Wall -Werror $(DEBUG)
OBJS_DIR=sources/objects
vpath %.c sources
vpath %.h headers
OBJS=$(OBJS_DIR)/pointers.o $(OBJS_DIR)/arrays.o $(OBJS_DIR)/bits.o $(OBJS_DIR)/etc.o $(OBJS_DIR)/menu.o $(OBJS_DIR)/main.o

$(EXEC) : $(OBJS)
	$(CC) $(CFLAGS) -o $@ $(OBJS)

$(OBJS_DIR)/pointers.o: pointers.c pointers.h
	$(CC) -c $(CFLAGS) $< -o $@
$(OBJS_DIR)/arrays.o: arrays.c arrays.h
	$(CC) -c $(CFLAGS) $< -o $@
$(OBJS_DIR)/bits.o: bits.c bits.h
	$(CC) -c $(CFLAGS) $< -o $@
$(OBJS_DIR)/etc.o: etc.c etc.h
	$(CC) -c $(CFLAGS) $< -o $@
$(OBJS_DIR)/menu.o: menu.c menu.h arrays.h bits.h pointers.h etc.h
	$(CC) -c $(CFLAGS) $< -o $@
$(OBJS_DIR)/main.o: main.c menu.h
	$(CC) -c $(CFLAGS) $< -o $@

clean: 
	rm -f $(EXEC)
	rm -f $(OBJS)