describe('Sessions (List)', () => {

  const mockSessions = [
    {
      id: 1,
      name: 'Yoga Morning',
      date: new Date().toISOString(),
      description: 'Relaxing session'
    },
    {
      id: 2,
      name: 'Yoga Evening',
      date: new Date().toISOString(),
      description: 'Advanced session'
    }
  ];

  const login = (isAdmin = true, sessions = mockSessions, status = 200) => {
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 200,
      body: {
        token: 'fake',
        type: 'Bearer',
        id: 1,
        username: 'userName',
        firstName: 'John',
        lastName: 'Doe',
        admin: isAdmin
      },
    }).as('login');

    cy.intercept('GET', '**/api/session', {
      statusCode: status,
      body: sessions
    }).as('sessions');

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test123');
    cy.get('button[type=submit]').click();
    cy.wait('@login');
    cy.url().should('include', '/sessions');
  };


  it('Display sessions list', () => {
    login(true);
    cy.contains('Yoga Morning').should('be.visible');
    cy.contains('Yoga Evening').should('be.visible');
  });


  it('Display admin actions when user is admin', () => {
    login(true);
    cy.contains('Create').should('be.visible');
    cy.contains('Edit').should('be.visible');
  });

  it('Hide admin actions when user is not admin', () => {
    login(false);
    cy.contains('Create').should('not.exist');
    cy.contains('Edit').should('not.exist');
  });

  it('Navigate to detail page', () => {
    login(true);
    cy.contains('Detail').first().click();
    cy.url().should('include', '/sessions/detail/');
  });


  it('Navigate to create page (admin)', () => {
    login(true);
    cy.contains('Create').click();
    cy.url().should('include', '/sessions/create');
  });

  it('Handle API error (sessions)', () => {
    login(true, [], 500);
    cy.get('.item').should('have.length', 0);
  });


  it('Display empty list', () => {
    login(false, []);
    cy.get('.item').should('have.length', 0);
  });

});

