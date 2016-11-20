# Brawl Bots
*programming by [@ssodelta](http://www.ssodelta.wordpress.com/)*

*[background tile pack](http://opengameart.org/content/topdown-tileset) by [@scribe](http://opengameart.org/users/scribe)*

*[planes](http://opengameart.org/content/top-down-planes-sprites-pack) by [@sujit717](http://unluckystudio.com/)*

*logo / ammo box by [@hsv3d](http://www.hsv3d.com/)*

Brawl Bots/AIArena (used interchangeably) is a bot-fighting framework written entirely in Java8. In essense, this software allows the automated fighting of virtual bots, whose behaviour is defined by a script. It includes a GUI for graphically watching the battles.

A bot consists of a small ship, which can be controlled by changing the rotation (left, right, no rotation), acceleration (forward, backwards, no change) and the bot can also fire projectiles at other bots. The goal of a bot is to shoot all other bots in the level, while not getting hit itself. A bot has a limited viewing range, and can only see objects directly in front of it (the view width can be changed).

https://www.youtube.com/watch?v=8sD7mTgA7og

The software in this repository has been used to develop a web application called [AIArena](http://www.ssodelta.com/bots/) which allows users to upload bots, and will automatically make them fight each other and rank them on the highscore table by winrate.

The software is still under active development, and as such may be prone to errors / be unstable at times. If you spot any errors, please don't hesitate to write to me at [admin@ssodelta.com](mailto:admin@ssodelta.com).

##License

This software (including all source files in this repository, unless otherwise noted) is written by Nikolaj Ignatieff Schwartzbach (ssodelta), all rights reserved, with help from @akiel123 and @bergieman. You are free to use or redistribute the source code (in original- or modified form) for non-commercial uses, provided you give credit to the original authors. 

No art for this project has been created by @ssodelta. To see the legal licenses for the art, please reach out to the respective authors.

It would be nice if you let me know if you intend to use the engine for your projects. You can reach me at [admin@ssodelta.com](mailto:admin@ssodelta.com).

##BOTScript

Ideally, users would upload a .java-file and then the server would compile it and test it. However, this raises some security issues due to execution of non-sandboxed Java code. Instead, Brawl Bots includes an interpreter for a simple, yet powerful, scripting language, dubbed BOTScript.

To fully be capable of writing your bot, I suggest reading this entire note as BOTScript is its own programming language, and may be more restricted than you think if you are used to writing in JavaScript, Python etc. To get a short of list of things to be mindful of, scroll down to '*Be mindful of...*'.

In BOTScript, all variables are modelled internally as the data-type 'double' of Java. It includes assignments, conditional branching (if-else, while) and method calls and function definitions (with variadic arguments). Variables are also scoped with respect to the current method being executed (more on that later).

BOTScript includes some special variables and methods which are used to gather input and control output. The full list is available at the end of this section.

To illustrate how BOTScript works, here's a very simple bot:

    #name Simple Bot;
    #method start:
        force    := 1;
        rotation := 1;

This bot does nothing but fly around in circles -- its force is positive, so it flies forward, and the rotation is also positive, so it is constantly turning counter-clockwise. A slightly more complicated bot to find and gather ammo is shown here:

    #name Ammo Hunter;
    #method start:
        force    := 1;
        
    #method update:
        rotation := 1;
        
    #method sight:
        if(sight_type == type_ammo){
            rotation := 0;
        }
        
It starts by flying forward (start), and then constantly turns around in a circle (update). If it sees any entity of type_ammo, then it does not rotate (this works because *sight* is called after *update*). The end result is a bot which constantly picks up ammunition, though it is arguably not effective at its task. 
    
##Method Declarations and Function Calls

BOTScript allows the declaration of methods with variadic number of arguments. Its evaluation strategy is [call-by-value](https://en.wikipedia.org/wiki/Evaluation_strategy#Call_by_value). To define a method, use the following syntax:

    #method METHOD_IDENTIFIER ( arg0, arg1, ... argn ):
        ...

If your function has no arguments, then the parentheses can be ommitted:

    #method METHOD_IDENTIFIER:
        ...

All subsequent lines after a method declaration will be interpreted as belonging to the method (until end-of-file or another method declaration). To call a method, any expression in BOTScript may include a call to a function. To illustrate this, we'll use the math utility functions:

    one := pow(cos(angle), 2) + pos(sin(angle), 2);

The return value of a method is decided by the special variable *return*. So when a method is called from within an expression, the method is first evaluated and then its value is interpreted as the value of the variable *return*. So we could define the fibonacci sequence:

    #method fib(n):
        if(n<=1){
            return := n;
        } else {
            return := n + fib(n-1);
        }

Note that setting the value of *return* does **not** terminate the method. The method will still run all the code in the method before returning its argument. By default, a method will return 0 (so if your function is **void** just don't set the value of *return* in the method).

The special method (predicate) **method** will evaluate whether or not its argument corresponds to a defined method. 

### Variables and Scope

Variables are scoped in BOTScript. Before executing a method, it remembers the state of all local variables, then executes the method, fetches the return values and replaces the entire set of variables with the old one.

Variables can be **global**, **local** or **permanent**. By default, assigning a value to a variable will result in a local declaration which will not exist outside of the current scope. However, if there exists a global/permanent variable with the same name as the local declaration, it will instead choose that variable. Permanent variables also take precedence over global variables, which takes precedence over local variables.

However, it is possible to explicitly state how a variable should be treated irregardless of the default behaviour:

* If you prepend a variable with the quote-mark ', it will be global for all methods (and will not overwrite any possible local variables).

* If a variable is prepended with the hat ^, it will be treated as local only, and if even if the variable also exists as a global variable, it will not be changed. 

* If a variable is prepended with $, it will be treated as a permanent variable and will be stored in a file, and can be accessed next game. This allows bots to build memory and learn patterns throughout its battles.

To illustrate decoration of variables, consider *lib_newpos()*:

    #method lib_newpos:
        'goal_x := arena_width / 2 + random() * random2() * arena_width / 2;
        'goal_y := arena_width / 2 + random() * random2() * arena_width / 2;
        return := 0;

Since the variables *goal_x* and *goal_y* are meant to be global, we force them to be global (otherwise, we would get an error when trying to access *goal_x* in an another method since they would not exist outside the method *lib_newpos*).

Similarly to **method**, there exists the predicate **var** which will evaluate whether or not its arguments corresponds to a defined variable, though it says nothing about scope. To check the scope of any variable, use predicates **local** or **global**.

Using this, we can extend Ammo Hunter to keep a counter of how many ammo containers it has picked up during its entire life-time:

    #name Ammo Hunter w. Memory;
    #method start:
        if(var(ammo_collected) == 0){
            $ammo_collected := 0;
        }
        force    := 1;
        
    #method update:
        rotation := 1;
        
    #method sight:
        if(sight_type == type_ammo){
            rotation := 0;
        }
    
    #method ammo:
        ammo_collected := ammo_collected + 1;

### Using maps

BOTScript also supports the usage of simple maps. That is, you can do:

    #method start:
        i := 0;
        map[i] := 5;

    #method update
        i := i+1;
        map[i] := map[i-1] + 1;

And each map[i] will be a separate variable. Maps are equivalent with variables with every unique integer value for a map corresponding to a different unique variable. BOTScript will first evaluate the inner expression (which may itself consist of map-accessing) and then compute a hash based on its value. The value will first be floored, then hashed. For a given map-access (mapName, mapInnerExpressionValue), the resulting variable string identifier will be:

    mapName + "_" + HASH(mapName) + "_" + HASH(mapInnerExpressionValue)

##Be mindful of...

BOTScript is not a profesionally designed language. It is designed by a first-year compsci student, and is thus equipped with its own set of restrictions compared to such versatile languages as JavaScript or Python. These "features" may be considered bugs, and many of them may be patched in a later update of BOTScript. But for now, be mindful of these constructs:

###If Else-statements
You can definitely write if-else statements in BOTScript. For instance:

    if(abs(bot_sight_angle_deg) <= 10){
        shoot := 1;
    } else {
        shoot := 0;
    }

Will shoot if the enemy bot is within 10 degrees of its view range, and not shoot otherwise. Let's assume our bot remembers the location of all bots using hashmaps. It would be helpful to shoot if we know an enemy bot is close by, as we may hit hit. So we could impose another check:

    if(abs(bot_sight_angle_deg) <= 10){
        shoot := 1;
    } else {
        if(can_probably_shoot_bot()){
            shoot := 1;
        } else {
            shoot := 0;
        }
    }

Notice that the second if-statement is nested. It is not possible to do the following construct in BOTScript (yet):

    if(abs(bot_sight_angle_deg) <= 10){
        shoot := 1;
    } else if(can_probably_shoot_bot()){
            shoot := 1;
    } else {
            shoot := 0;
    }

###Boolean expressions

It is not possible to write ! in front of a boolean expression to negate it. 

##Library Methods

Lots of logic of bots is tedious to code and has thus been abstracted away and is included in the [library "lib"](https://github.com/akiel123/AIArena/blob/master/lib.bot). To include the library, include a line "#include lib;" before your method declarations. To write a bot which flies around randomly on the map, we can use the library method *lib_targeting()*. *lib_targeting* keeps track of two variables; *goal_x*, and *goal_y* corresponding to a point on the map. It will then figure whether or not it should turn left or right, and when facing its target, it'll fly forward. It couples well with the other method *lib_newpos* which chooses *goal_x* and *goal_y* randomly. So we could have a bot:

    #name Search;
    #include lib;
    #method start:
        lib_newpos();
    
    #method update:
        lib_targeting();
    
Which will first choose a random target, turn towards it and fly towards the target. We might also want the bot to choose a new target when sufficiently close to its current - *lib_targeting* already does exactly this; calls *lib_newpos();* when the distance to *(goal_x, goal_y)* is less than *bot_radius*.

This makes it easy to write powerful bots. So we could write a more efficient version of "Ammo Hunter" like:

    #name Ammo Hunter v2;
    #include lib;
    #method start:
        lib_newpos();
    
    #method update:
        lib_targeting();
    
    #method sight:
        if(sight_type == type_ammo){
            lib_target_sight(0);
        }

The method *lib_target_sight(0)* merely calls *goal_x := sight_x; goal_y := sight_y;*.

####Full list of library methods:

 * **lib_aux_anglediff( *rad1*, *rad2* )** - returns the difference in radians between two angles (in radians).
 * **lib_aux_nicerad( *rad* )** - maps an angle *rad* in radians to the interval [0, 2pi].
 * **lib_canhit_sight()** - return 1 if shooting a bullet will hit the sight (assuming they continue with the same velocity).
 * **lib_dist_to_goal()** - returns the distance in pixels to the current target.
 * **lib_decide_rotation( *current*, *goal* )** - assuming we're pointing in some angle *current* and want to point in angle *goal* which way should we rotate?
 * **lib_newpos()** - sets a new random target. The target will not be outside the margins of the arena, and there will be a bias towards choosing points closer to the center.
 * **lib_targeting()** - rotates to turn towards target, and if pointing towards it, flies forward.
 * **lib_target_sight( *future* )** - sets to target to the position of the sight *future* frames ahead  (assuming it continues with the same velocity).
 * **lib_turn_towards( *angle* )** - rotates the bot to point towards some angle *angle*.
 * **lib_turn_away_from( *angle* )** - rotates the bot to point away from some angle *angle*.

####Full list of predicates:

Predicates are special functions which treat its single argument as a String and checks if the script engine contains the string as some sort of data. Predicates evaluate to 1 if the object exists in the script engine, and 0 otherwise.

 * **global ( *variable* )** - Will check if *variable* exists as a global variable.
 * **local ( *variable* )** - Will check if *variable* exists as a local variable in the current scope. 
 * **method ( *method* )** - Will check if *method* corresponds to a defined method.
 * **permanent ( *variable* )** - Will check if *variable* exists as a permanent variable for this bot.
 * **var ( *variable* )** - Will check if *variable* exists as a variable in the current scope.

Note that predicates will *not* evaluate their arguments contrary to all other methods in BOTScript. So doing nonsense like:

    #method nonsense:
        var(2+3);
        var(nonsense + 5);

... does not work. However, it will not trigger a ScriptRuntimeException, and merely return 0 because the strings "2+3" and "nonsense + 5" are not defined as variables.

BOTScript also includes most of the utility functions from the [Java Math library](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html) with a full list available [in the source code](https://github.com/akiel123/AIArena/blob/master/src/com/brawlbots/botscript/expression/MathSysLibrary.java#L30).

If you write a neat method for your bot, which you think other people may find of use, please don't hesitate to write to me at [admin@ssodelta.com](mailto:admin@ssodelta.com).

##Special Methods:

To control the flow of control, the framework of BotBrawl calls some special methods in the scripts. These are:

* **ammo**   - Is called once for every ammo container the bot picks up.
* **hurt**   - Is called once for every time a bot is hit by a bullet. 
* **sight**  - Is called once for each entity the bot can see in its range of vision. Some specific variables (sight_type, sight_x etc.) are updated prior to each call. This method is called after *update* is called.
* **start**  - Is called once when a bot is spawned.
* **update** - Is called once at each frame. A lot of environment variables change prior to this method. 
* **end** - Is called either when the bot dies, or when the bot wins (can be checked using bot_health).

###Special Variables:

These variables remain constant throughout the game.

* **arena_height** - The height of the arena (in pixels).
* **arena_margin** - The inner margin of the arena (in pixels).
* **arena_width** - The width of the arena (in pixels).
* **e** - Euler's constant (2.7182817...)
* **type_ammo** - Type-number representing an ammo container.
* **type_bullet** - Type-number representing bullet belonging to other bots (bullets from the bot itself do not trigger this type).
* **type_enemy** - Type-number representing hostile units (ie. other bots).
* **type_friend** - Type-number representing friendly units (not currently in use).
* **type_wall** - Type-number representing a wall (not currently in use).
* **pi** - 3.14159265...

These variables get updated just before *update* is called.

* **bot_ammo** - Number of remaining projectiles
* **bot_angle_deg** - The rotation of the bot in degrees (0 = 360 = right).
* **bot_angle_rad** - The rotation of the bot in radians (0 = 2pi = right).
* **bot_health** - The remaining health of the bot.
* **bot_radius** - The radius (in pixels) of the bot.
* **bot_reload_time** - The reload time (in frames) of the bot.
* **bot_speed_angle_deg** - The rotation of the velocity vector in degrees.
* **bot_speed_angle_rad** - The rotation of the velocity vector in radians.
* **bot_speed_dx** - The horizontal change in position at each frame.
* **bot_speed_dy** - The vertical change in position at each frame.
* **bot_speed_mag** - The magnitude of the velocity vector (in pixels/frame).
* **bot_viewsize_deg** - The width in degrees in which the bot can detect other entities to either side.
* **bot_viewsize_rad** - The width in radians in which the bot can detect other entities to either side.
* **bot_x** - The *x*-coordinate of the bot (in pixels).
* **bot_y** - The *y*-coordinate of the bot (in pixels).

These variables are specific to the *sight*-method. These get updated just before each call of *sight* (with a sighting representing one entity spotted at this frame).

* **sight_angle_deg** - The difference in degrees between where the bot is pointing and where this sighting was spotted.
* **sight_angle_rad** - The difference in radians between where the bot is pointing and where this sighting was spotted.
* **sight_dist** - The distance in pixels between the bot and the sighting.
* **sight_dx** - The horizontal component of the sight's velocity vector.
* **sight_dy** - The vertical component of the sight's velocity vector.
* **sight_id** - Every bullet, ammo-container and bot have unique ids. This variable represents the id of the sighting that was spotted.
* **sight_type** - The type of sighting spotted. For instance, it can be used to check if we've spotted an enemy (ie. 'if(sight_type == type_enemy){ ... }').
* **sight_x** - The absolute *x*-position of the sighting.
* **sight_y** - The absolute *y*-position of the sighting.
