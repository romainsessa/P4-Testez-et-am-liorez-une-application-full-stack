describe('Session Form (Create / Update)', () => {

    const mockTeachers = [
        { id: 1, firstName: 'John', lastName: 'Doe' },
        { id: 2, firstName: 'Jane', lastName: 'Smith' }
    ];

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

    const login = (isAdmin = true) => {
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
            statusCode: 200,
            body: mockSessions
        }).as('sessions');

        cy.visit('/login');
        cy.get('input[formControlName=email]').type('yoga@studio.com');
        cy.get('input[formControlName=password]').type('test123');
        cy.get('button[type=submit]').click();
        cy.wait('@login');
        cy.url().should('include', '/sessions');
    };

    const setupForm = ({
        isAdmin = true,
        teachers = mockTeachers,
        teachersStatus = 200,
        sessionStatus = 200,
        saveStatus = 200,
        mode = 'create' // 'create' | 'update'
    } = {}) => {

        login(isAdmin);

        // teachers
        cy.intercept('GET', '**/api/teacher', {
            statusCode: teachersStatus,
            body: teachers
        }).as('teachers');

        // detail (update)
        if (mode === 'update') {
            cy.intercept('GET', '**/api/session/1', {
                statusCode: sessionStatus,
                body: {
                    id: 1,
                    name: 'Yoga Morning',
                    date: new Date().toISOString(),
                    description: 'Relaxing session',
                    teacher_id: 1
                }
            }).as('detail');
        }

        // save
        cy.intercept(
            mode === 'create' ? 'POST' : 'PUT',
            mode === 'create' ? '**/api/session' : '**/api/session/1',
            {
                statusCode: saveStatus,
                body: {
                    id: 1,
                    name: 'Yoga Morning Saved',
                    date: new Date().toISOString(),
                    description: 'Relaxing session',
                    teacher_id: 1
                }
            }
        ).as('save');

        if (mode === 'create') {
            cy.contains('button', 'Create').click();
        } else if (mode == 'update') {
            cy.contains('button', 'Edit').first().click();
        }

        cy.wait('@teachers');

        if (mode === 'update') {
            cy.wait('@detail');
        }
    };

    it('Create session successfull', () => {

        setupForm({ mode: 'create' });

        cy.get('input[formControlName=name]').type('Yoga Test');
        cy.get('input[formControlName=date]').type('2026-01-01');

        cy.get('mat-select[formControlName=teacher_id]').click();
        cy.get('mat-option').first().click();

        cy.get('textarea[formControlName=description]')
            .type('Session description test');

        cy.get('button[type=submit]').click();

        cy.wait('@save');

        cy.url().should('include', '/sessions');
    });


    it('Update session successfull', () => {

        setupForm({ mode: 'update' });

        cy.get('input[formControlName=name]')
            .clear()
            .type('Updated Yoga');

        cy.get('button[type=submit]').click();

        cy.wait('@save');

        cy.url().should('include', '/sessions');
    });


    it('Form validation', () => {

        setupForm({ mode: 'create' });

        cy.get('button[type=submit]').should('be.disabled');

        cy.get('input[formControlName=name]').type('Test');

        cy.get('button[type=submit]').should('be.disabled');
    });

    it('Create session failed', () => {

        setupForm({ mode: 'create', saveStatus: 500 });

        cy.get('input[formControlName=name]').type('Yoga Test');
        cy.get('input[formControlName=date]').type('2026-01-01');

        cy.get('mat-select').click();
        cy.get('mat-option').first().click();

        cy.get('textarea[formControlName=description]')
            .type('Error test');

        cy.get('button[type=submit]').click();

        cy.wait('@save');

        cy.url().should('include', '/sessions/create');
    });


    it('Update session failed', () => {

        setupForm({ mode: 'update', saveStatus: 500 });

        cy.get('button[type=submit]').click();

        cy.wait('@save');

        cy.url().should('include', '/sessions/update/1');
    });
});