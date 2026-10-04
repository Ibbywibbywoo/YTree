import { useEffect, useState } from "react";
import axios from "axios";

const API = "http://localhost:8080/api/accounts";

type Status = "UPLOADED" | "MISSING" | "OUTDATED";

interface Provider {
  id: string;
  name: string;
  status: Status;
  statementFilename: string | null;
  statementDate: string | null;
}

const lightTheme = {
  bg: "#f6f7f5",
  surface: "#ffffff",
  surface2: "#fbfcfb",
  ink: "#1b211e",
  muted: "#5e6862",
  faint: "#8a938c",
  hair: "#e3e6e1",
  hairStrong: "#d3d8d2",
  accent: "#1e6b54",
  accentSoft: "#e8f0ec",
  warn: "#9a6410",
  warnSoft: "#f6ecd8",
  bad: "#a83c3a",
  badSoft: "#f4e2e1",
  ok: "#1e6b54",
  okSoft: "#e6f0ea",
};

const darkTheme = {
  bg: "#141815",
  surface: "#1c211d",
  surface2: "#191e1a",
  ink: "#eaede8",
  muted: "#9ea79f",
  faint: "#79837b",
  hair: "#2c332e",
  hairStrong: "#384038",
  accent: "#56b593",
  accentSoft: "#1d2c26",
  warn: "#d5a44a",
  warnSoft: "#322a17",
  bad: "#dd8582",
  badSoft: "#33211f",
  ok: "#56b593",
  okSoft: "#1d2c26",
};

function StatusPill({ status, theme }: { status: Status; theme: typeof lightTheme }) {
  const styles: Record<Status, { bg: string; color: string; dot: string }> = {
    UPLOADED: { bg: theme.okSoft, color: theme.ok, dot: theme.ok },
    MISSING: { bg: theme.badSoft, color: theme.bad, dot: theme.bad },
    OUTDATED: { bg: theme.warnSoft, color: theme.warn, dot: theme.warn },
  };
  const s = styles[status];
  return (
      <span style={{ background: s.bg, color: s.color, padding: "3px 10px", borderRadius: 999, fontSize: 12.5, fontWeight: 600, display: "inline-flex", alignItems: "center", gap: 6 }}>
      <span style={{ width: 6, height: 6, borderRadius: "50%", background: s.dot, display: "inline-block" }} />
        {status.charAt(0) + status.slice(1).toLowerCase()}
    </span>
  );
}

export default function App() {
  const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
  const [dark, setDark] = useState(prefersDark);
  const theme = dark ? darkTheme : lightTheme;

  const [providers, setProviders] = useState<Provider[]>([]);
  const [available, setAvailable] = useState<string[]>([]);
  const [filter, setFilter] = useState<string>("ALL");
  const [showAdd, setShowAdd] = useState(false);
  const [selected, setSelected] = useState<string[]>([]);
  const [submitMsg, setSubmitMsg] = useState<string | null>(null);

  const fetchProviders = async () => {
    const res = await axios.get(`${API}/providers`);
    setProviders(res.data);
  };

  const fetchAvailable = async () => {
    const res = await axios.get(`${API}/available`);
    setAvailable(res.data);
  };

  useEffect(() => {
    fetchProviders();
    fetchAvailable();
  }, []);

  // Listen for system theme changes
  useEffect(() => {
    const mq = window.matchMedia("(prefers-color-scheme: dark)");
    const handler = (e: MediaQueryListEvent) => setDark(e.matches);
    mq.addEventListener("change", handler);
    return () => mq.removeEventListener("change", handler);
  }, []);

  const handleAdd = async () => {
    for (const name of selected) {
      await axios.post(`${API}/providers`, { name });
    }
    setSelected([]);
    setShowAdd(false);
    await fetchProviders();
    await fetchAvailable();
  };

  const handleRemove = async (id: string) => {
    await axios.delete(`${API}/providers/${id}`);
    fetchProviders();
    fetchAvailable();
  };

  const handleUpload = async (id: string) => {
    const filename = `statement_${id}_${Date.now()}.pdf`;
    await axios.post(`${API}/providers/${id}/upload`, { filename });
    fetchProviders();
  };

  const handleSubmit = async () => {
    try {
      const res = await axios.post(`${API}/submit`);
      setSubmitMsg(res.data.message);
    } catch (err: any) {
      setSubmitMsg(err.response?.data || "Submission failed.");
    }
  };

  const filtered = filter === "ALL" ? providers : providers.filter(p => p.status === filter);
  const canSubmit = providers.length > 0 && providers.every(p => p.status === "UPLOADED");
  const readyCount = providers.filter(p => p.status === "UPLOADED").length;

  return (
      <div style={{ background: theme.bg, minHeight: "100vh", fontFamily: "system-ui, sans-serif", padding: "40px 20px", transition: "background 0.2s, color 0.2s" }}>
        <div style={{ maxWidth: 760, margin: "0 auto" }}>

          {/* Header */}
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: 32 }}>
            <div>
              <h1 style={{ fontFamily: "Georgia, serif", fontSize: "2rem", margin: "0 0 6px", color: theme.ink }}>Connect your accounts</h1>
              <p style={{ color: theme.muted, margin: 0 }}>{readyCount} of {providers.length} ready</p>
            </div>
            <button
                onClick={() => setDark(!dark)}
                style={{ background: theme.surface, border: `1px solid ${theme.hairStrong}`, borderRadius: 8, padding: "6px 14px", cursor: "pointer", fontSize: 13, color: theme.muted, fontWeight: 600 }}>
              {dark ? "☀️ Light" : "🌙 Dark"}
            </button>
          </div>

          {/* Card */}
          <div style={{ background: theme.surface, border: `1px solid ${theme.hair}`, borderRadius: 14, padding: "22px 22px 26px", boxShadow: "0 1px 2px rgba(27,33,30,.05), 0 8px 24px rgba(27,33,30,.05)" }}>

            {/* Filter */}
            <select value={filter} onChange={e => setFilter(e.target.value)}
                    style={{ fontSize: 13, color: theme.muted, border: `1px solid ${theme.hairStrong}`, borderRadius: 8, padding: "5px 11px", marginBottom: 12, background: theme.surface }}>
              <option value="ALL">All statuses</option>
              <option value="UPLOADED">Uploaded</option>
              <option value="MISSING">Missing</option>
              <option value="OUTDATED">Outdated</option>
            </select>

            {/* Provider rows */}
            {filtered.map(p => (
                <div key={p.id} style={{ display: "grid", gridTemplateColumns: "1.1fr auto 1fr auto", alignItems: "center", gap: 14, padding: "13px 4px", borderTop: `1px solid ${theme.hair}` }}>
                  <span style={{ fontWeight: 600, color: theme.ink }}>{p.name}</span>
                  <StatusPill status={p.status} theme={theme} />
                  <span style={{ fontFamily: "monospace", fontSize: 12.5, color: theme.faint }}>{p.statementFilename || ""}</span>
                  <div style={{ display: "flex", gap: 8, justifyContent: "flex-end" }}>
                    <button onClick={() => handleUpload(p.id)}
                            style={{
                              fontSize: 12.5,
                              fontWeight: 600,
                              padding: "5px 13px",
                              borderRadius: 8,
                              border: p.status === "MISSING" ? `1px solid ${theme.hairStrong}` : `1px solid ${theme.warn}`,
                              background: p.status === "MISSING" ? theme.surface2 : theme.warnSoft,
                              color: p.status === "MISSING" ? theme.ink : theme.warn,
                              cursor: "pointer"
                            }}>
                      {p.status === "MISSING" ? "Upload" : "Replace"}
                    </button>
                    <button onClick={() => handleRemove(p.id)}
                            style={{ border: "none", background: "transparent", color: theme.faint, fontSize: 17, cursor: "pointer", padding: "4px 6px", borderRadius: 7 }}>
                      ×
                    </button>
                  </div>
                </div>
            ))}

            {/* Footer */}
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginTop: 20 }}>
              <button onClick={() => setShowAdd(!showAdd)}
                      style={{ fontSize: 12.5, fontWeight: 600, padding: "5px 13px", borderRadius: 8, border: `1px dashed ${theme.hairStrong}`, background: "transparent", color: theme.muted, cursor: "pointer" }}>
                + Add provider
              </button>
              <button onClick={handleSubmit} disabled={!canSubmit}
                      style={{ background: theme.accent, color: dark ? "#141815" : "#06120d", border: `1px solid ${theme.accent}`, padding: "9px 20px", borderRadius: 9, fontWeight: 600, fontSize: 13.5, opacity: canSubmit ? 1 : 0.45, cursor: canSubmit ? "pointer" : "not-allowed" }}>
                Submit
              </button>
            </div>

            {/* Add provider panel */}
            {showAdd && (
                <div style={{ marginTop: 16, padding: 16, background: theme.bg, borderRadius: 10, border: `1px solid ${theme.hair}` }}>
                  <p style={{ margin: "0 0 10px", fontWeight: 600, fontSize: 14, color: theme.ink }}>Select providers to add:</p>
                  {available.map(name => (
                      <label key={name} style={{ display: "block", marginBottom: 6, fontSize: 14, cursor: "pointer", color: theme.ink }}>
                        <input type="checkbox" value={name}
                               checked={selected.includes(name)}
                               onChange={e => setSelected(prev => e.target.checked ? [...prev, name] : prev.filter(n => n !== name))}
                               style={{ marginRight: 8 }} />
                        {name}
                      </label>
                  ))}
                  <button onClick={handleAdd} disabled={selected.length === 0}
                          style={{ marginTop: 10, background: theme.accent, color: "#fff", border: "none", padding: "7px 16px", borderRadius: 8, fontWeight: 600, fontSize: 13, cursor: selected.length > 0 ? "pointer" : "not-allowed", opacity: selected.length > 0 ? 1 : 0.5 }}>
                    Add {selected.length > 0 ? `(${selected.length})` : ""}
                  </button>
                </div>
            )}

            {/* Submit message */}
            {submitMsg && (
                <div style={{ marginTop: 16, padding: "12px 16px", borderRadius: 10, background: submitMsg.includes("success") ? theme.okSoft : theme.badSoft, color: submitMsg.includes("success") ? theme.ok : theme.bad, fontWeight: 600, fontSize: 14 }}>
                  {submitMsg}
                </div>
            )}
          </div>
        </div>
      </div>
  );
}