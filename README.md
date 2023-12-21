# Integrated Development Environment
*Authors:* Kirill Shirokov, Vlad Em

### How to run
todo

### Parts description
#### Backend (Kirill Shirokov)
Implementation details:
 
1. By now there are two main interfaces through which the frontend can interract with the backend. (`VirtualFile` and `FilesystemView`) These two interfaces do encapsulate two of responsibilities of the IDE. Those are *file editing* and *project management*. On *file editing* modification operations like `insert` or `delete`, the signal should go through the handlers in `Vfs` instead of current simple implementation.

2. There are two types of signals in the system - internal and external. External signals are coming from a special `FilesystemMonitor` component, which looks for changes in the project folder in the filesystem. Internal signals are coming from the user and are processed one by one in a background thread of `Vfs`.

3. In order to separate two processes described in p.2 in such way that they won't influence each other, I decided to create special workflow of handling changes:
3.1. If the signal is external => process it and update the state of the `Vfs`
3.2. If the signal is internal and can be transformed in external (save file, create file/folder, delete file, etc) => transform
3.3. If the signal is internal and occurs a lot (edits in files) => make changes locally and write to disk on demand

4. Becides the `Vfs` and surroundings, I implemented a Lexer for the language. It is implemented with two iterations of parsing inside, can capture *Undefined char subsequences* and outputs a list of `LexerToken`-s to be parsed further

5. Very simple Parser was also implemented. Right now it works correctly and doesn't hold on correct programs without `if` statements and comments. I have thoughts about possible error recovery there, but several cases.

6. Both Lexer and Parser are parts of the `Psi` component and do work based of the `Vfs`. In order to syncronize them and avoid concurrent access to reads and writes I use global *Read-Write lock* from the `Vfs`. Handlers from `Vfs` do work only with write lock, while `Psi` uses read lock.

Further development:
1. Recovery strategies implementation
2. File editing transition under write locks
3. `watches` management of files to update in `Psi`.
4. Lexer highlighter, Parser highlighter and others
5. Scopes, variables and typecheckers implementation in `Psi`

#### Frontend (Vlad Em)
## Features

- **Text Editing**: A simple text editor with support for basic navigation and editing, including arrow key movement, backspace, delete, and text insertion.
- **File Saving**: Files after editing can be easily saved by pressing the button in the left bottom corner.
- **Dark Mode**: A sleek dark theme to reduce eye strain and provide a modern aesthetic.
- **File Browser**: Navigate your project's files and folders with an integrated file browser.
- **Key Command Handling**: Special handling for key combinations like Cmd + arrow for a more native feel.
- **Caret Movement**: Navigation between lines using arrows is implemented (all directions).
- **Viewport and scrollbar**: Scrolling feature is constructed.

## Structure

- `Main.kt`: The entry point of the application. Sets up the window and handles key events.
- `frontend/App.kt`: Contains the main editor UI, rendering text and handling user input.
- `frontend/KeyEvents.kt`: Processes key inputs and translates them into actions like moving the caret or editing text.
- `caret`: Handles the logic for moving the caret in the text editor and its drawing.
- `files`: Implements the file browser functionality.

## How to run:

