insert into Users (first_name, last_name, email, password)
            Values ('Jon', 'Doe', 'jon.doe@bcgdv.com', 'whoami'),
            ('Jane', 'Doe', 'jane.doe@bcgdv.com', 'pwd'),
            ('John', 'Hancock', 'john.hancock@bcgdv.com', 'mount');

insert into Users (first_name, middle_name, last_name, email, password)
            Values ('Billy', 'Bob', 'Burger', 'billy.b.burger@bcgdv.com', 'touch');

insert into Accounts (user_id, balance)
            Values (1, 100.00),
            (2, -50.00),
            (3, 1000.00),
            (4, 50.00);
