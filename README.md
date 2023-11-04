# README

## PostgreSQL

If the postgre sql installation is not in the environemt variable PATH, add it to the PATH;

Then execute psql from the power shell.

List all databases with the user `david`:
```
psql.exe -U david -l
```

Login to database weather_app:

```
psql.exe -U david -d weather_app
```

List all relations (tables) in the database:
```
\d
```

List schema of table:
```
\d <table name>
```


