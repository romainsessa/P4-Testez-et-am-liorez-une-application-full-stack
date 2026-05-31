describe('Session Detail', () => {

    const mockSession = {
        id: 1,
        name: 'Yoga Morning',
        date: new Date().toISOString(),
        description: 'Relaxing session',
        teacher_id: 1,
        users: [2], // participants
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
    };

    const mockTeacher = {
        id: 1,
        firstName: 'John',
        lastName: 'DOE'
    };

    const loginAndGoToDetail = (
        {
            isAdmin = false,
            users = [2],
            detailStatus = 200
        } = {}
    ) => {

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
            }
        }).as('login');

        cy.intercept('GET', '**/api/session', {
            statusCode: 200,
            body: [{
                id: 1,
                name: 'Yoga'
            }]
        }).as('sessions');

        cy.intercept('GET', '**/api/session/1', {
            statusCode: detailStatus,
            body: {
                ...mockSession,
                users: users
            }
        }).as('detail');

        cy.intercept('GET', '**/api/teacher/1', {
            statusCode: 200,
            body: mockTeacher
        }).as('teacher');

        cy.visit('/login');

        cy.get('input[formControlName=email]').type('yoga@studio.com');
        cy.get('input[formControlName=password]').type('test123');
        cy.get('button[type=submit]').click();

        cy.wait('@login');
        cy.wait('@sessions');

        cy.contains('Detail').click();

        cy.wait('@detail');
        cy.wait('@teacher');
    };


    it('Display session detail', () => {

        loginAndGoToDetail();

        cy.contains('Yoga Morning').should('be.visible');
        cy.contains('Relaxing session').should('be.visible');
        cy.contains('John DOE').should('be.visible');
    });

    it('Delete session (admin)', () => {

        cy.intercept('DELETE', '**/api/session/1', {
            statusCode: 200
        }).as('delete');

        loginAndGoToDetail({ isAdmin: true });

        cy.contains('Delete').click();

        cy.wait('@delete');

        cy.location('pathname').should('eq', '/sessions');
    });

    it('Participate to session', () => {

        cy.intercept('POST', '**/api/session/1/participate/1', {
            statusCode: 200
        }).as('participate');

        loginAndGoToDetail({ users: [] });

        cy.contains('Participate').click();

        cy.wait('@participate');
        cy.wait('@detail');
    });

    it('UnParticipate to session', () => {

        cy.intercept('DELETE', '**/api/session/1/participate/1', {
            statusCode: 200
        }).as('unParticipate');

        loginAndGoToDetail({ users: [1] });

        cy.contains('Do not participate').click();

        cy.wait('@unParticipate');
        cy.wait('@detail');
    });


    it('Show correct buttons for user', () => {

        loginAndGoToDetail({ isAdmin: false, users: [] });

        cy.contains('Participate').should('be.visible');
        cy.contains('Do not participate').should('not.exist');
        cy.contains('Delete').should('not.exist');
    });

    it('Show correct buttons for admin', () => {

        loginAndGoToDetail({ isAdmin: true });

        cy.contains('Delete').should('be.visible');
        cy.contains('Participate').should('not.exist');
    });

});