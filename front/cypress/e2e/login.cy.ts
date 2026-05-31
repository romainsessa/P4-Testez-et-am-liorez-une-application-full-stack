describe('Auth flow', () => {

  describe('Login', () => {

    it('Login successfull', () => {
      cy.intercept('POST', '**/api/auth/login', {
        statusCode: 200,
        body: {
          token: 'fake',
          id: 1,
          username: 'userName',
          firstName: 'John',
          lastName: 'Doe',
          admin: true
        },
      }).as('login');

      cy.intercept('GET', '**/api/session', []).as('session');

      cy.visit('/login');

      cy.get('input[formControlName=email]').type('yoga@studio.com');
      cy.get('input[formControlName=password]').type('test123');

      cy.get('button[type=submit]').click();

      cy.wait('@login');
      cy.wait('@session');

      cy.url().should('include', '/sessions');
    });

    it('Login failed - backend error', () => {
      cy.intercept('POST', '**/api/auth/login', {
        statusCode: 401,
      }).as('loginFail');

      cy.visit('/login');

      cy.get('input[formControlName=email]').type('wrong@test.com');
      cy.get('input[formControlName=password]').type('wrong');

      cy.get('button[type=submit]').click();

      cy.wait('@loginFail');

      cy.get('.error').should('be.visible');
    });

    it('Login - form validation', () => {
      cy.visit('/login');

      cy.get('button[type=submit]').should('be.disabled');

      cy.get('input[formControlName=email]').type('invalid-email');
      cy.get('input[formControlName=password]').type('12');

      cy.get('button[type=submit]').should('be.disabled');
    });

  });

  describe('Register', () => {

    it('Register successfull', () => {
      cy.intercept('POST', '**/api/auth/register', {
        statusCode: 200,
        body: {}
      }).as('register');

      cy.visit('/register');

      cy.get('input[formControlName=firstName]').type('John');
      cy.get('input[formControlName=lastName]').type('Doe');
      cy.get('input[formControlName=email]').type('john@test.com');
      cy.get('input[formControlName=password]').type('test123');

      cy.get('button[type=submit]').click();

      cy.wait('@register');

      cy.url().should('include', '/login');
    });

    it('Register failed - backend error', () => {
      cy.intercept('POST', '**/api/auth/register', {
        statusCode: 400,
      }).as('registerFail');

      cy.visit('/register');

      cy.get('input[formControlName=firstName]').type('John');
      cy.get('input[formControlName=lastName]').type('Doe');
      cy.get('input[formControlName=email]').type('john@test.com');
      cy.get('input[formControlName=password]').type('test123');

      cy.get('button[type=submit]').click();

      cy.wait('@registerFail');

      cy.get('.error').should('be.visible');
    });

    it('Register - form validation', () => {
      cy.visit('/register');

      cy.get('button[type=submit]').should('be.disabled');

      cy.get('input[formControlName=firstName]').type('Jo');
      cy.get('input[formControlName=lastName]').type('Do');
      cy.get('input[formControlName=email]').type('invalid');
      cy.get('input[formControlName=password]').type('12');

      cy.get('button[type=submit]').should('be.disabled');
    });

  });

  describe('Navigation', () => {

    it('Access login page', () => {
      cy.visit('/login');
      cy.contains('Login').should('be.visible');
    });

    it('Access register page', () => {
      cy.visit('/register');
      cy.contains('Register').should('be.visible');
    });

  });

});