# andriod app - drawing database

User can use finger to make drawings on screen ("ink" follows users finger). The user can draw lines of multiple different colors and sizes. An UNDO button is also present that allows the user to backtrack each succesive line/stroke (between which the user lifted finger off-screen).

Each drawing can be saved (using an internal & onboard SQLite Database), and then re-opened & modifed on later runs of the app. The layering of each stroke/line in the order it was drawn is preserved when it is stored.
