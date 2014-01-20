i18nextResourceGenerator
========================

This plugin provides a context menu entry to generate resource files for i18next.

Project Settings
----------------

Translateable strings should have this format

    Locale.get(namespace:key.value)

Folder of languages

    /resources/i18next/locales/

Default placeholder

    "" (empty string)

Example
-------

Input:

    Locale.get(app:datatypes.exp),
    Locale.get(app:nameofmyapp)

Output in folder *lang*:
    
    /resources/i18next/locales/lang/app.json

    {
        "datatypes": {
            "exp": ""
        },
        "nameofmyapp": ""
    }

##Licence

The MIT License (MIT)

Copyright (c) 2014 DREEBIT GmbH

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.