import { TestBed, ComponentFixture } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ListComponent } from './list.component';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { SessionService } from '../../../../core/service/session.service';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

describe('ListComponent (integration)', () => {
    let fixture: ComponentFixture<ListComponent>;
    let component: ListComponent;
    let httpMock: HttpTestingController;

    const mockSessionService = {
        sessionInformation: {
            id: 1,
            admin: true,
            token: '',
            type: '',
            username: '',
            firstName: '',
            lastName: ''
        }
    };

    const mockSessions = [
        {
            id: 1,
            name: 'Yoga',
            date: new Date(),
            description: 'Relax',
            teacher_id: 1,
            users: [],
            createdAt: new Date(),
            updatedAt: new Date()
        }
    ];

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [
                ListComponent
            ],
            providers: [
                provideHttpClient(),
                provideHttpClientTesting(),
                provideRouter([]),
                SessionApiService,
                { provide: SessionService, useValue: mockSessionService }
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(ListComponent);
        component = fixture.componentInstance;

        httpMock = TestBed.inject(HttpTestingController);

        fixture.detectChanges();
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('should call API and get sessions', () => {
        const req1 = httpMock.expectOne('api/session');
        req1.flush(mockSessions);

        component.sessions$.subscribe(data => {
            expect(data.length).toBe(1);
            expect(data[0].name).toBe('Yoga');
        });

        const req2 = httpMock.expectOne('api/session');
        req2.flush(mockSessions);
    });


    it('should expose user info from SessionService', () => {
        const req = httpMock.expectOne('api/session');
        req.flush([]);
        expect(component.user?.id).toBe(1);
        expect(component.user?.admin).toBe(true);
    });

    it('should handle empty session list', () => {
        const req1 = httpMock.expectOne('api/session');
        req1.flush([]);

        component.sessions$.subscribe(data => {
            expect(data.length).toBe(0);
        });

        const req2 = httpMock.expectOne('api/session');
        req2.flush([]);
    });
});