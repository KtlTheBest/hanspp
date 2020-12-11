# hanspp
Our first attempt at writing a language.

Hello everyone, if you visited this project, all I hope is that it would be of much help to you and you'll find what you were searching for. The language is simple and is very limited in capabilities (so much, it has it's own stack machine to run on). The work that is presented here was rushed. A LOT. So it is by no means a perfect example of how the compiler should be written. In fact, there are many parts in the code, where assumptions are made and some bits of code appear in places they don't belong. As I said, the language is very limited, but it works! It can even calculate the sine using the Taylor series! If you can add syscalls, importing mechanism, global and static variables to it, then it will be a full-fledged language of it's own (not sure about the performance though).

## Grammar
About the grammar of the language... it's messy. There are extensive examples in the `testN.cfl` files, but since it's not obvious, there are no brackets, only `begin` and `end` and the last statement before the `end` doesn't have a semicolon (although every other line has, it even includes the inner `end` blocks). There are no default values, you must always give some value to the variables, otherwise you'll have a message like `type null : Type was is not well-formed` or something. And the `void` functions must always have a `return` at the end, even though they don't have anything to return. Those are specifics of a stack machine.

## Cflat and the naming
The name of the repository is `hanspp`, is because we've given the name at the time when we didn't know the name of the language. The professor, who did all of the heavy-lifting for us, named it a `cflat`. Well, I think that `hanspp` looks better. Either `hanspp` or `cflat`, it's up to you, as long as they point to the same thing (pointers, eh).

## How to run?
In the repository, there is a `Makefile`. There are still some changes that should be made to make it work like a compiler and accept filename as an argument, but for now set the name of the file you want to compile in `Main.java` and then run:
```
make
```
That should do the trick.

## Troubleshooting
* This project uses java, so make sure that you have compatible java version, e.g. keep installing and uninstalling until you make it compile the compiler.
* This project uses jflex, so make sure you have that installed.
* This project uses CUP, it has a zip file with jar files for the cup. Extract them if they are not present yet.

If the above tips don't help you... Google it. And please let me know, I would like to fix the bug some time later.

Happy cflatting :)
