import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ListComponent } from './list.component';
import { SessionService } from 'src/app/core/service/session.service';
import { SessionApiService } from 'src/app/core/service/session-api.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('ListComponent (unit)', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;

  const mockSessions = [
    {
      id: 1,
      name: 'Yoga',
      description: 'Relax',
      date: new Date()
    }
  ];

  const mockSessionServiceAdmin = {
    sessionInformation: { admin: true }
  };

  const mockSessionServiceUser = {
    sessionInformation: { admin: false }
  };

  const mockSessionApiService = {
    all: jest.fn()
  };

  beforeEach(async () => {
    mockSessionApiService.all.mockReturnValue(of(mockSessions));

    await TestBed.configureTestingModule({
      imports: [ListComponent, RouterTestingModule],
      providers: [
        { provide: SessionService, useValue: mockSessionServiceAdmin },
        { provide: SessionApiService, useValue: mockSessionApiService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load sessions', () => {
    component.sessions$.subscribe((sessions) => {
      expect(sessions.length).toBe(1);
      expect(sessions[0].name).toBe('Yoga');
    });
  });

  it('should show create button if admin', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Create');
  });

  it('should show edit button if admin', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Edit');
  });

});