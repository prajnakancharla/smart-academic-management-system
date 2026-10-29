
import { Link } from "react-router-dom";
import { ArrowLeft, GraduationCap } from "lucide-react";

export default function NotFound() {
  return (
    <main className="not-found-page">
      <div className="not-found-icon"><GraduationCap size={34} /></div>
      <span className="eyebrow">PAGE NOT FOUND</span>
      <h1>We couldn't find that page.</h1>
      <p>The page may have moved or the address may be incorrect.</p>
      <Link className="primary-link" to="/dashboard">
        <ArrowLeft size={17} /> Return to dashboard
      </Link>
    </main>
  );
}
