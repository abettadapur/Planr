FunTimes Server Component
============================
To get started with the server

1. Create a virtual environment and install the packages
     
         pip install virtualenv
         virtualenv env
         source env/bin/activate
         pip install -r requirements.txt

2. Create a configuration file
        
        cp funtimes/etc/config.py.sample funtimes/etc/config.py
    
    Fill in the API fields with tokens. The database entries can be left as default

3. Create the database and run the migration scripts
    
        python manage.py db upgrade
        
4. Run the server
    
        python serve.py
